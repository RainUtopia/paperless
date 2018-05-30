#ifndef LIB_MEETINTREFACE_MODULE_H
#define LIB_MEETINTREFACE_MODULE_H


#ifdef __cplusplus
extern "C" {
#endif

#include "base.h"
#include "meetc/meetinterface_type.h"

	//检查该程序有没有运行另一个实例
	//检查出错返回-1 有另一个实例在运行返回1 没有实例运行返回0
	int meetcore_checkselfrun();

//程序类型
#define MEET_PROGRAM_TYPE_MEETCLIENT	 1 //会议终端软件
#define MEET_PROGRAM_TYPE_PROJECTIVE	 2 //投影软件
#define MEET_PROGRAM_TYPE_STREAMCAPTURE  3 //流采集软件
#define MEET_PROGRAM_TYPE_SERVICE		 4 //茶水服务软件
#define MEET_PROGRAM_TYPE_ANDROIDAPP	 5 //android APP
#define MEET_PROGRAM_TYPE_CLIENTDEBUG	 6 //TCP客户端调试
#define MEET_PROGRAM_TYPE_PUBLISHER	 7 //会议发布软件

	//会议模块回调数据接口
	//phdr 回调的数据 数据结构是 Type_HeaderInfo + 后接上真实的数据
	typedef int(*MEETCORE_CB)(pType_HeaderInfo phdr, void* puser);

	// 会议模块日志回调数据接口
#define MEETCORE_LOGLEVEL_ERROR     0//错误
#define MEETCORE_LOGLEVEL_INFO      1//信息
#define MEETCORE_LOGLEVEL_COMMON    2//信息
#define MEETCORE_LOGLEVEL_WARNING   3//警告
#define MEETCORE_LOGLEVEL_DEBUG     4//调试

	//logflag
#define MEETLOG_FLAG_LOGTOFILE  0x00000001 //保存到日志文件
#define MEETLOG_FLAG_PRINTFOUNT 0x00000002 //输出到console

	//loglevel 日志等级
	//pmsg 回调的数据
	typedef void(*MEETCORE_LOGCB)(int loglevel, const char* pmsg, void* puser);

	typedef struct 
	{
		const char* pconfigpathname;//client.ini文件路径名
		int			streamnum;//初始化的流通道数

		void*		   puser;//用户指针 回调函数中会通过 void* puser 返回该指针
		MEETCORE_CB    pfunc;//回调函数

		int			   blogtofile;//参见 logflag
		MEETCORE_LOGCB plogfunc;//日志回调函数

		int			usekeystr;//使用传入的程序标识,
		char		keystr[64];//程序标识,给那些无法获取唯一标识的设备使用

		int			programtype;//程序类型 用于区分程序来初始化接口数据
	}MeetCore_InitParam, *pMeetCore_InitParam;

	//初始化会议模块
	//0表示成功,-1表示失败
	int meetcore_init(pMeetCore_InitParam pa);

//oper flag 处理数据返回的操作标志
#define MEETCORE_CALL_FLAG_JUSTRETURN		 0 //表示将数据返回给调用方
#define MEETCORE_CALL_FLAG_JUSTCALLBACK		 1 //表示将数据通过回调返回
#define MEETCORE_CALL_FLAG_CALLBACKANDRETURN 2 //表示将数据同时传递到回调也要返回数据

	//pdata数据
	//pretdata 返回的数据 数据结构是 Type_HeaderInfo + 后接上真实的数据
	//operflag 指示数据的操作标志 参见MEETCORE_CALL_FLAG_JUSTRETURN 定义
	//返回负数表示错误, 0表示没有返回值, 正值表示pretdata的大小
	int meetcore_Call(pType_HeaderInfo pdata, pType_HeaderInfo* pretdata, int32u operflag);

	//type 请求类型
	//用于释放meetcore_call 的pretdata资源
	void meetcore_free(pType_HeaderInfo pdata);
	
	//获取type 对应的字符串 eg:type=TYPE_MEET_INTERFACE_TIME --> "TYPE_MEET_INTERFACE_TIME"
	const char* meetcore_GettypeStr(int32u type);

	//获取method 对应的字符串 eg:method=METHOD_MEET_INTERFACE_NOTIFY --> "METHOD_MEET_INTERFACE_NOTIFY"
	const char* meetcore_GetMethodStr(int32u method);

	//获取error 对应的字符串 eg:error=ERROR_MEET_INTERFACE_NOTINIT --> "ERROR_MEET_INTERFACE_NOTINIT"
	const char* meetcore_GetErrorStr(int32u err);

	//获取status 对应的字符串 eg:status=STATUS_ACCESSDENIED --> "STATUS_ACCESSDENIED"
	const char* meetcore_GetStatusMsg(int32u status);

	//输出日志,如果设置了日志回调函数,将日志回调给用户设置的回调函数
	void meetcore_printf(int loglevel, const char *pszFmt, ...);

#ifdef __cplusplus
}
#endif


#endif