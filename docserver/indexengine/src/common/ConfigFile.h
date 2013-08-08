// ConfigFile.h : ConfigFile
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-03

#ifndef CONFIGFILE_H
#define CONFIGFILE_H

#include <stdint.h>
#include <string>
#include <map>

#define CONFIG_DEFAULT_SESSION ""

typedef std::map<std::string, std::string> STR_STR_MAP;
typedef STR_STR_MAP::iterator STR_STR_MAP_ITER;
typedef std::map<std::string, STR_STR_MAP *> CONFIG_MAP;
typedef CONFIG_MAP::iterator CONFIG_MAP_ITER;

/* Configure file format:
 *
 *     [session_name]
 *     # comment line
 *     key = value
 */
class ConfigFile {

public:
	ConfigFile();
	~ConfigFile();

	int LoadFile(const char *file);
	const char *GetValue(const char *session, const char *key, const char *def = NULL) const;
	int GetIntValue(const char *session, const char *key, int def = 0) const { return (int)GetInt64Value(session, key, def); }
	int64_t GetInt64Value(const char *session, const char *key, int64_t def = 0) const;

	// parse one line
	// if line is session name, store to session parameter
	// if line valid config item, add to specified session
	int ParseLine(std::string *session, const std::string &line);

	void SetValue(const char *session, const char *key, const char *value);

	ConfigFile(const ConfigFile &conf);
	ConfigFile &operator=(const ConfigFile &conf);

	std::string Dump(void) const;

	static ConfigFile *GlobalInstance();

private:
	CONFIG_MAP config_map_;
};

#endif // CONFIGFILE_H
