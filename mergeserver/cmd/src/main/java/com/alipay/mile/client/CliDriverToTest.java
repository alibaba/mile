/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import jline.ArgumentCompletor;
import jline.ClassNameCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.FileNameCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.alipay.mile.Config;
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.client.result.MileUpdateResult;
import com.alipay.mile.client.result.MileExportResult;
import com.alipay.mile.message.DocStartCommandMessage;
import com.alipay.mile.message.DocStopCommandMessage;
import com.alipay.mile.message.MergeStartCommandMessage;
import com.alipay.mile.message.MergeStartLeadQueryMessage;
import com.alipay.mile.message.MergeStopCommandMessage;
import com.alipay.mile.message.MergeStopLeadQueryMessage;
import com.alipay.mile.message.Message;

/**
 * 回归测试专用单线程
 * @author jin.qian
 * @version $Id: CliDriverToTest.java, v 0.1 2011-5-18 上午09:51:28 jin.qian Exp $
 */
public class CliDriverToTest {

    public final static String  PROMPT  = "mile";
    public final static String  PROMPT2 = "    ";                                           // when ';' is not yet seen
    private static final Logger LOGGER  = Logger.getLogger(CliDriverToTest.class.getName());

    // 命令的参数选项
    private Options             options = new Options();

    private boolean             debugMode;

    private ConsoleReader       reader;

    private CommandLineParser   parser  = new PosixParser();

    private ApplationClientImpl applationClientImpl;

    private String              command;

    private String              timeOut;
    //    private String[]            parms;
    private String              execSql;
    private String              filePath;
    private String              resultFile;

    protected void setDefultOption() {

        options.addOption("t", true, "current timeOut"); // 参数不可用
        //        options.addOption("x", true, "result File"); // 参数可用
        options.addOption("s", true, "sql code"); // 参数可用
        options.addOption("f", true, "filePath"); // 参数可用
        options.addOption("h", true, "help"); // 参数可用
        options.addOption("p", true, "parms"); // 参数可用
        options.addOption("m", true, "mergerServierIp"); // 参数可用
        options.addOption("d", true, "docServierIp"); // 参数可用
        options.addOption("c", true, "query copy servers");
    }

    /**
     * @throws IOException
     * 设置关键字
     */
    protected void setDefultConsoleReager() throws IOException {
        List<Completor> completors = new ArrayList<Completor>();
        completors.add(new SimpleCompletor(new String[] { "query", "insert", "update", "delete", "export",
                "startd", "stopd", "startm", "stopm", "quit", "exit", "startleadquery",
                "stopleadquery" }));
        completors.add(new FileNameCompletor());
        completors.add(new ClassNameCompletor());
        completors.add(new NullCompletor());
        reader.addCompletor(new ArgumentCompletor(completors));

        reader.getHistory().setHistoryFile(
            new java.io.File(System.getProperty("user.home", ".") + File.separator
                             + ".CliDriverToTest.history"));
        reader.getHistory().setMaxSize(100);
    }

    /**
     * 显示帮助信息
     */
    public void printUsage(String cmd) {
        LOGGER.error(cmd);
        LOGGER
            .error("Usage: query |insert|update|delete [<-s sql>][-p split ','] [-t 3000] [-help]");
        LOGGER.error("");
        LOGGER.error("Usage: load [-f fileName]");
        LOGGER.error("");
        LOGGER.error("Usage: quit | exit");
        LOGGER.error("  -t <TimeOut>    TimeOut from command line");
        LOGGER.error("  -f <filename>   Sql from files");
        LOGGER.error("  -S              Sql from command line");
        LOGGER.error("  -p              Sql parms from line");
        LOGGER.error("  -h              user help");
        LOGGER.error("");
        LOGGER.error("-s and -f cannot be specified together. In the absence of these");
        LOGGER.error("options, interactive shell is started");
        LOGGER.error("");

    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    /**
     * 解析命令行参数
     *
     * @param args
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    public void processCmd(String cmd) {

        try {
            String[] strTemp = strSplit(cmd.trim());
            if (strTemp.length > 0) {
                command = strTemp[0].trim().toLowerCase();
            }
            execSql = "";
            filePath = "";
            resultFile = "";
            timeOut = "";
            String[] parms = null;
            CommandLine commandLine = parser.parse(options, strTemp);
            if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
            if (commandLine.hasOption("s")) {
                execSql = commandLine.getOptionValue("s").trim();
            }
            if (commandLine.hasOption("p")) {
                parms = commandLine.getOptionValue("p").trim().split(",");
            }
            if (commandLine.hasOption("f")) {
                filePath = commandLine.getOptionValue("f").trim();
            }
            if (commandLine.hasOption("t")) {
                timeOut = commandLine.getOptionValue("t").trim();
            } else {
                timeOut = "3000";
            }
            if (commandLine.hasOption("s") && commandLine.hasOption("f")) {
                LOGGER.error("-s -f 不能同时出现");
            }
            if (commandLine.hasOption("h")) {
                printUsage(cmd);
            }

            if (command.equalsIgnoreCase("query") || command.equalsIgnoreCase("insert")
                || command.equalsIgnoreCase("delete") || command.equalsIgnoreCase("update")
				|| command.equalsIgnoreCase("export")) {
                if (commandLine.hasOption("s") && !commandLine.hasOption("p")) {
                    List<String> p = new ArrayList<String>();
                    execSql = SqlParamParser.parseStringParams(execSql, p);
                    parms = (String[]) p.toArray(new String[0]);
                }
            }

            if (command.equalsIgnoreCase("query") && commandLine.hasOption("s")) {
                if (!chackCommand(command, execSql)) {
                    LOGGER.error("输入错误：");
                    return;
                }
                MileQueryResult mileQueryResult = applationClientImpl.preQueryForList(execSql,
                    SqlParamParser.stringsToObjects(parms), Integer.parseInt(timeOut));
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("查询是否成功：" + mileQueryResult.isSuccessful());
                }
                List<Map<String, Object>> queryResult = mileQueryResult.getQueryResult();
                if (queryResult != null && queryResult.size() > 0) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("返回的记录数：" + queryResult.size());
                    }
                    Map<String, Object> headmap = queryResult.get(0);
                    StringBuffer headstr = new StringBuffer();
                    for (String str : headmap.keySet()) {
                        headstr.append(str);
                        headstr.append(", ");
                    }
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(headstr.toString());
                    }
                    for (Map<String, Object> tmpmap : queryResult) {
                        StringBuffer rsstr = new StringBuffer();
                        for (String str : tmpmap.keySet()) {
                            rsstr.append(tmpmap.get(str) == null ? "null" : tmpmap.get(str));
                            rsstr.append(", ");
                        }
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info(rsstr.toString());
                        }
                    }
                } else {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("返回的记录数：0");
                    }
                }

            } else if (command.equalsIgnoreCase("insert") && commandLine.hasOption("s")) {
                if (!chackCommand(command, execSql)) {
                    LOGGER.error("输入错误：");
                    return;
                }
                MileInsertResult mileInsertResult = applationClientImpl.preInsert(execSql,
                    SqlParamParser.stringsToObjects(parms), Integer.parseInt(timeOut));
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("插入是否成功：" + mileInsertResult.isSuccessful());
                    LOGGER.info("插入DocID：" + mileInsertResult.getDocId());
                }
            } else if (command.equalsIgnoreCase("delete") && commandLine.hasOption("s")) {
                if (!chackCommand(command, execSql)) {
                    LOGGER.error("输入错误：");
                    return;
                }
                MileDeleteResult mileDeleteResult = applationClientImpl.preDelete(execSql,
                    SqlParamParser.stringsToObjects(parms), Integer.parseInt(timeOut));
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("删除是否成功：" + mileDeleteResult.isSuccessful());
                    LOGGER.info("删除记录数：" + mileDeleteResult.getDeleteNum());
                }
            } else if (command.equalsIgnoreCase("update") && commandLine.hasOption("s")) {
                if (!chackCommand(command, execSql)) {
                    LOGGER.error("输入错误：");
                    return;
                }
                MileUpdateResult mileUpdateResult = applationClientImpl.preUpdate(execSql,
                    SqlParamParser.stringsToObjects(parms), Integer.parseInt(timeOut));
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("更新是否成功：" + mileUpdateResult.isSuccessful());
                    LOGGER.info("更新记录数：" + mileUpdateResult.getUpdateNum());
                }
			} else if (command.equalsIgnoreCase("export") && commandLine.hasOption("s")) {
				if (!chackCommand(command, execSql)) {
                    LOGGER.error("输入错误：");
					return;
				}
				MileExportResult mileExportResult =
					applationClientImpl.preExport(execSql,
												  SqlParamParser.stringsToObjects(parms),
												  Integer.parseInt(timeOut));
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("导出成功：" + mileExportResult.isSuccessful());
					LOGGER.info("导出记录：" + mileExportResult.getExportNum());
				}
            } else if (command.equalsIgnoreCase("load") && commandLine.hasOption("f")) {
                //                execSqlfromFile(commandLine.getOptionValue("f").trim());
            } else if (command.equalsIgnoreCase("startd") && commandLine.hasOption("m")
                       && commandLine.hasOption("d")) {
                String[] mergeServers = commandLine.getOptionValue("m").trim().split(",");
                Message commandMessage = new DocStartCommandMessage(commandLine.getOptionValue("d")
                    .trim());
                String rs = applationClientImpl.command(mergeServers, commandMessage);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(rs);
                }
            } else if (command.equalsIgnoreCase("startm") && commandLine.hasOption("m")) {
                String[] mergeServers = commandLine.getOptionValue("m").trim().split(",");
                Message commandMessage = new MergeStartCommandMessage();
                String rs = applationClientImpl.command(mergeServers, commandMessage);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(rs);
                }
            } else if (command.equalsIgnoreCase("stopd") && commandLine.hasOption("m")
                       && commandLine.hasOption("d")) {
                String[] mergeServers = commandLine.getOptionValue("m").trim().split(",");
                Message commandMessage = new DocStopCommandMessage(commandLine.getOptionValue("d")
                    .trim());
                String rs = applationClientImpl.command(mergeServers, commandMessage);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(rs);
                }
            } else if (command.equalsIgnoreCase("stopm") && commandLine.hasOption("m")) {
                String[] mergeServers = commandLine.getOptionValue("m").trim().split(",");
                Message commandMessage = new MergeStopCommandMessage();
                String rs = applationClientImpl.command(mergeServers, commandMessage);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(rs);
                }
            } else if (command.equalsIgnoreCase("startleadquery") && commandLine.hasOption("m")
                       && commandLine.hasOption("c")) {
                String[] srcServers = commandLine.getOptionValue("m").trim().split(",");
                String[] desServers = commandLine.getOptionValue("c").trim().split(",");
                Message commandMessage = new MergeStartLeadQueryMessage();
                ((MergeStartLeadQueryMessage) commandMessage).setMergeServers(Arrays
                    .asList(desServers));

                String rs = applationClientImpl.command(srcServers, commandMessage);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(rs);
                }
            } else if (command.equalsIgnoreCase("stopleadquery") && commandLine.hasOption("m")) {
                String[] srcServers = commandLine.getOptionValue("m").trim().split(",");
                Message commandMessage = new MergeStopLeadQueryMessage();

                String rs = applationClientImpl.command(srcServers, commandMessage);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(rs);
                }
            }

        } catch (NumberFormatException e) {
            LOGGER.error(e);
            printUsage(cmd);
        } catch (ParseException e) {
            LOGGER.error(e);
            printUsage(cmd);
        } catch (IOException e) {
            LOGGER.error(e);
            printUsage(cmd);
        } catch (Exception e) {
            LOGGER.error(e);
            printUsage(cmd);
        }
    }

    /**
     * 输入命令检查
     * @param command
     * @param commandLine
     * @return
     */
    private boolean chackCommand(String command, String commandLine) {
        boolean falg = false;
        if (command.toUpperCase(Locale.US).equals("QUERY")) {
            falg = commandLine.toLowerCase(Locale.US).startsWith("select");
        } else if (command.toUpperCase(Locale.US).equals("UPDATE")) {
            falg = commandLine.toLowerCase(Locale.US).startsWith("update");
        } else if (command.toUpperCase(Locale.US).equals("DELETE")) {
            falg = commandLine.toLowerCase(Locale.US).startsWith("delete");
        } else if (command.toUpperCase(Locale.US).equals("INSERT")) {
            falg = commandLine.toLowerCase(Locale.US).startsWith("insert");
		} else if (command.toUpperCase(Locale.US).equals("EXPORT")) {
            falg = commandLine.toLowerCase(Locale.US).startsWith("export");
        }

        return falg;
    }

    /**
     * @param line
     * @throws NumberFormatException
     * @throws Exception
     * 执行单行SQL
     */
    public void processLine(String line) throws NumberFormatException {
        for (String oneCmd : line.split(";")) {
            processCmd(oneCmd);
        }
    }

    private static String[] strSplit(String str) {
        String[] data = str.split("-");
        for (int i = 1; i < data.length; i++) {
            data[i] = "-" + data[i];
        }
        return data;
    }

    public void start() throws NumberFormatException, IOException {

        while (true) {
            String str = reader.readLine(PROMPT + ">");
            if (str != null && str.length() > 0) {
                processLine(str);
            }
        }

    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public void setReader(ConsoleReader reader) {
        this.reader = reader;
    }

    public CommandLineParser getParser() {
        return parser;
    }

    public void setParser(CommandLineParser parser) {
        this.parser = parser;
    }

    public ApplationClientImpl getApplationClientImpl() {
        return applationClientImpl;
    }

    public void setApplationClientImpl(ApplationClientImpl applationClientImpl) {
        this.applationClientImpl = applationClientImpl;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getExecSql() {
        return execSql;
    }

    public void setExecSql(String execSql) {
        this.execSql = execSql;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * 分析输入参数加载配置文件
     * @param args
     * @return
     * @throws ArgumentFormantException
     */
    private static Map<String, String> parseArguments(final String[] args) {
        Map<String, String> config = new HashMap<String, String>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
            } else if (arg.startsWith("-")) {
                arg = arg.substring(1);
            }
            String[] keyValue = arg.split("=");
            if (keyValue == null || keyValue.length != 2) {
                LOGGER.error("ERROE Argument[" + arg + "] Format Error.");
            } else {
                config.put(keyValue[0], keyValue[1]);
            }
        }
        return config;
    }

    /**
     * @param args
     * 控制台调用入口
     */
    public static void main(String[] args) {

        Properties props = System.getProperties();
        props.put("jline.WindowsTerminal.directConsole", "false");
        try {
            Map<String, String> config = parseArguments(args);
            BasicConfigurator.configure();
            if (config.get("log4j") != null) {
                PropertyConfigurator.configure(config.get("log4j"));
            } else {
                PropertyConfigurator.configure("etc" + File.separator
                                               + Config.CONFIG_DEFAULT_MILE_LOG_PROPERTIES);
            }
            CliDriverToTest cliDriver = new CliDriverToTest();
            cliDriver.setDebugMode(false);
            cliDriver.setReader(new ConsoleReader());
            cliDriver.setDefultOption();
            cliDriver.setDefultConsoleReager();
            cliDriver.setApplationClientImpl(new ApplationClientImpl());
            if (config.get("cliconfig") != null) {
                cliDriver.getApplationClientImpl().readProperties(config.get("cliconfig"));
            } else {
                String filePath = System.getProperty("user.dir") + File.separator + "etc"
                                  + File.separator + "mileCliClent.properties.prod";
                cliDriver.getApplationClientImpl().readProperties(filePath);
            }
            cliDriver.getApplationClientImpl().init();
            

            if (args.length == 0) {
                cliDriver.start();
            } else {
                cliDriver.processLine(StringUtils.join(args, " "));
                System.exit(0);
            }
            

        } catch (NumberFormatException e) {
            LOGGER.error("", e);
        } catch (IOException e) {
            LOGGER.error("", e);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

}
