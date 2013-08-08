#!/usr/bin/env python

import optparse
import os, atexit, sys, time
import ConfigParser
import subprocess
import logging
import signal

port = 12340
config_file = ""
log_file ='doc_run.log'
command = ""
homedir = ""
running = True
global logger, daemon

def readConfig(file='etc/doc_run.conf'):
	global command, config_file, homedir, port, log_file
	Config = ConfigParser.ConfigParser()
	Config.read(file)
	sections = Config.sections()
	for section in sections:
		for item in Config.items(section):
			if "command" == item[0]:
				command = item[1]
			elif "config_file" == item[0]:
				config_file = item[1]
			elif "work_space" == item[0]:
				homedir = item[1]
			elif "log_file" == item[0]:
				log_file = item[1]
			elif "port" == item[0]:
				port = item[1]


class Daemon:
	"""
	A generic daemon class.
	
	"""
	def __init__(self, pidfile, docpidfile,stdin='/dev/null', stdout='/dev/null', stderr='/dev/null', home='/tmp'):
		self.stdin = stdin
		self.stdout = stdout
		self.stderr = stderr
		self.pidfile = pidfile
		self.home = home
		self.subprocess = True
		self.docpidfile = docpidfile

	
	def daemonize(self):
		logger.debug("start daemon")
		try: 
			pid = os.fork() 
			if pid > 0:
				# exit first parent
				sys.exit(0) 
		except OSError, e: 
			logger.error("fork #1 failed: %d (%s)\n" % (e.errno, e.strerror))
			sys.exit(1)
	
		# decouple from parent environment
		os.chdir(self.home)
		os.setsid() 
		os.umask(0) 
	
		# do second fork
		try: 
			pid = os.fork() 
			if pid > 0:
				# exit from second parent
				sys.exit(0) 
		except OSError, e: 
			logger.error("fork #2 failed: %d (%s)\n" % (e.errno, e.strerror))
			sys.exit(1) 

		logger.debug("redirect standard file descriptors")
		# redirect standard file descriptors
		sys.stdout.flush()
		sys.stderr.flush()
		si = file(self.stdin, 'r')
		so = file(self.stdout, 'a+')
		se = file(self.stderr, 'a+', 0)
		os.dup2(si.fileno(), sys.stdin.fileno())
		os.dup2(so.fileno(), sys.stdout.fileno())
		os.dup2(se.fileno(), sys.stderr.fileno())
	
		# write pidfile
		atexit.register(self.delpid)
		pid = str(os.getpid())
		logger.debug("write pidfile:%s" % self.pidfile)
		file(self.pidfile,'w+').write("%s\n" % pid)
		logger.debug("daemon end")
	
	def delpid(self):
		os.remove(self.pidfile)
		os.remove(self.docpidfile)

	def start(self):
		"""
		Start the daemon
		"""
		# Check for a pidfile to see if the daemon already runs
		try:
			pf = file(self.pidfile,'r')
			pid = int(pf.read().strip())
			pf.close()
		except IOError:
			pid = None
	
		if pid:
			message = "pidfile %s already exist. Daemon already running?\n"
			sys.stderr.write(message % self.pidfile)
			sys.exit(1)
		
		# Start the daemon
		sys.stdout.write("daemonizing myself ... \n")
		sys.stdout.write("start " + command + "...\n")
		self.daemonize()
		self.run()

	def stop(self):
		"""
		Stop the daemon
		"""
		# Get the pid from the pidfile
		try:
			pf = file(self.pidfile,'r')
			pid = int(pf.read().strip())
			pf.close()
			docpf = file(self.docpidfile,'r')
			docpid = int(docpf.read().strip())
			docpf.close()
		except IOError:
			pid = None
			docpid = None
	
		if not pid:
			message = "pidfile %s does not exist. Daemon not running?\n"
			sys.stderr.write(message % self.pidfile)
			return # not an error in a restart

		# Try killing the daemon process	
		sys.stdout.write("stop " + command + "...\n")
		try:
			while 1:
				os.kill(pid, signal.SIGTERM)
				os.kill(docpid,signal.SIGTERM)
				time.sleep(0.1)
		except OSError, err:
			err = str(err)
			if err.find("No such process") > 0:
				if os.path.exists(self.pidfile):
					os.remove(self.pidfile)
					os.remove(self.docpidfile)
			else:
				logger.error(str(err))
				sys.exit(1)

	def restart(self):
		"""
		Restart the daemon
		"""
		self.stop()
		self.start()

	def run(self):
		#run docserver in another process
		args = []
		if os.path.exists("./"+command):
			args.append("./" + command)
		else:
			args.append(command)
		args.append("-f")
		args.append(config_file)
		args.append("-p")
		args.append(port)
		logger.debug(args)
		logger.debug("into run")
		
		sub = subprocess.Popen(args, shell=False, close_fds=True, stdin=None, stdout=None, stderr=None)
		file(self.docpidfile,'w+').write("%s\n" % sub.pid)
		crashes = 0
		logger.info("into while loop")
		while True:
			ret = subprocess.Popen.poll(sub)
			if ret == 0:
				logger.info("subprocess " + str(sub.pid) + " exit normally")
				break;		
			elif ret == None:
				#running
				if not running:
					#kill itself
					subprocess.Popen.terminate(sub)
					logger.info("subprocess " + str(sub.pid) + " is killed")
					sys.exit(0)
			else:
				#crashed
				crashes = crashes + 1
				if crashes > 10:
					logger.error("subprocess " + str(sub.pid) + " crashes more then 10 times")
					sys.exit(0)
				logger.error("subprocess " + str(sub.pid) + " crashes " + str(crashes) + " times")
				logger.error("return code: " + str(ret))
				#start another subprocess
				sub = subprocess.Popen(args, shell=False, close_fds=True, stdin=None, stdout=None, stderr=None)
				file(self.docpidfile,'w+').write("%s\n" % sub.pid)
			time.sleep(1)

def do_exit(signal, frame):
	global running
	logger.info("terminate subprocess")
	running = False
	#time.sleep(1)
	#sys.exit(0)

def main():
	#options parser
	global command, config_file, homedir, logger, daemon, log_file
	p = optparse.OptionParser(description="doc_run.py: keep docserver running",
			prog="doc_run.py",
			version="version:0.1",
			#usage="Python %prog -c config_file [-d homedir] [-b] -k [start|stop|restart]")
			usage="Python %prog -c config_file [-d homedir] -k [start|stop|restart]")
	p.add_option('--config', '-c')
	p.add_option('--dirhome', '-d')
	#p.add_option('-b', action="count")
	p.add_option('-k', choices=["start", "stop", "restart"])
	p.set_defaults(config = 'etc/doc_run.conf')
	p.set_defaults(dirhome = sys.path[0])
	p.set_defaults(log_file = 'doc_run.log')
	options, arguments = p.parse_args()
	if options.dirhome:
		homedir = options.dirhome
	if options.config:
		readConfig(options.config)

	print "path:" + homedir
	#judge path
	if os.path.exists(homedir):
		os.chdir(homedir)
	else:
		print "path:" + homedir + " does not exist!\n"
		sys.exit(2)
	if not os.path.exists(command):
		print "command:" + command + " does not exist!\n"
		sys.exit(2)
	if not os.path.exists(config_file):
		print "config_file:" + command + " does not exist!\n"
		sys.exit(2)

	#init logger
	logger = logging.getLogger()
	#create file handler
	try:
		os.makedirs( os.path.dirname(log_file))
	except os.error:
		pass
	hdlr = logging.FileHandler(log_file)
	formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
	hdlr.setFormatter(formatter)
	logger.addHandler(hdlr)
	#print all information
	#logger.setLevel(logging.NOTSET)
	#print info error
	logger.setLevel(logging.INFO)

	#start daemon
	daemon = Daemon('run/python.pid','run/docserver.pid', home=homedir)
	if "start" == options.k:
		daemon.start()
	elif "stop" == options.k:
		daemon.stop()
	elif "restart" == options.k:
		daemon.restart()
	else:
		p.print_help()


if __name__ == '__main__':
	signal.signal(signal.SIGINT, do_exit) 
	signal.signal(signal.SIGTERM, do_exit) 
	main()
