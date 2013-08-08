// ConfigFile.cpp : ConfigFile
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-03

#include "ConfigFile.h"
#include "log.h"

#include <iostream>
#include <fstream>

#include <boost/algorithm/string.hpp>
#include <boost/lexical_cast.hpp>

ConfigFile::ConfigFile()
{
	// Set default session
	config_map_.insert(std::make_pair(CONFIG_DEFAULT_SESSION, new STR_STR_MAP()));
}

ConfigFile::~ConfigFile()
{
	// Delete all STR_STR_MAP
	for (CONFIG_MAP_ITER it = config_map_.begin(); it != config_map_.end(); ++it) {
		delete it->second;
	}
}

ConfigFile::ConfigFile(const ConfigFile &conf)
{
	CONFIG_MAP::const_iterator it = conf.config_map_.begin();
	for (; it != conf.config_map_.end(); ++it) {
		STR_STR_MAP *m = new STR_STR_MAP(*it->second);
		config_map_.insert(std::make_pair(it->first, m));
	}
}

ConfigFile &ConfigFile::operator=(const ConfigFile &conf)
{
	ConfigFile t(conf);
	std::swap(t.config_map_, config_map_);
	return *this;
}

void ConfigFile::SetValue(const char *session, const char *key, const char *value)
{
	const char *s = (NULL == session) ? CONFIG_DEFAULT_SESSION : session;
	STR_STR_MAP *m = NULL;
	CONFIG_MAP_ITER iter = config_map_.find(s);
	if (iter == config_map_.end()) {
		m = new STR_STR_MAP();
		config_map_.insert(std::make_pair(s, m));
	}
	else
		m = iter->second;
	(*m)[key] = value;
}

int ConfigFile::LoadFile(const char *file)
{

	std::ifstream is(file);
	if (!is) {
		// no way to get detail error info. (use exception only get a basic_ios::clear error)
		// use errno?
		log_error("open %s failed", file);
		return -1;
	}

	std::string session(CONFIG_DEFAULT_SESSION);
	int line_nu = 0;
	while (!is.eof()) {
		std::string line;
		std::getline(is, line);
		line_nu++;

		if (ParseLine(&session, line) != 0) {
			log_error("parse config file [%s] failed, line number [%d]", file, line_nu);
			return -1;
		}
	}

	return 0;
}

int ConfigFile::ParseLine(std::string *session, const std::string &l)
{
	std::string line(l); // make a copy
	boost::trim(line);
	if (line.empty() || boost::starts_with(line, "#")) // empty or comment line
		return 0;

	// is session
	if (boost::starts_with(line, "[")) {
		if (!boost::ends_with(line, "]")) {
			log_error("bad session name  %s", line.c_str());
			return -1;
		}

		*session = line.substr(1, line.size() - 2);
		boost::trim(*session);
		// not needed to insert session here.
		return 0;
	}

	size_t pos = line.find_first_of("=");
	if (pos == std::string::npos) {
		log_error("invalid config, content: %s", line.c_str());
		return -1;
	}

	std::string key = line.substr(0, pos);
	std::string value = line.substr(pos + 1);
	boost::trim(key);
	boost::trim(value);

	SetValue(session->c_str(), key.c_str(), value.c_str());
	return 0;
}

const char *ConfigFile::GetValue(const char *session, const char *key, const char *def) const
{
	CONFIG_MAP::const_iterator it = config_map_.find(session);
	if (it == config_map_.end())
		return def;
	STR_STR_MAP::const_iterator iter = it->second->find(key);
	if (iter == it->second->end())
		return def;
	return iter->second.c_str();
}

int64_t ConfigFile::GetInt64Value(const char *session, const char *key, int64_t def) const
{
	const char *value = GetValue(session, key, NULL);
	if (NULL == value)
		return def;
	return atoll(value);
}

std::string ConfigFile::Dump(void) const
{
	std::string dump;
	CONFIG_MAP::const_iterator it = config_map_.begin();
	for (; it != config_map_.end(); ++it) {
		dump += "[" + it->first + "]\n";
		STR_STR_MAP::const_iterator iter = it->second->begin();
		for (; iter != it->second->end(); ++iter) {
			dump += iter->first + " = " + iter->second + "\n";
		}
	}
	return dump;
}

ConfigFile *ConfigFile::GlobalInstance()
{
	static ConfigFile g_conf;
	return &g_conf;
}
