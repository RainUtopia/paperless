#ifndef _INTERFACE_NET_PACKET_PBHANDLE_H
#define _INTERFACE_NET_PACKET_PBHANDLE_H

#include "base.h"
#include "meetc/libMeetInterface.h"


//数据更新回调
/*
type: 操作类型
method:操作方法
pdata：类型、方法对应的数据，不需要释放
datalen：数据的大小 字节
puser：用户指针
*/
typedef int(*meetPB_callback)(int32u type, int32u method, void* pdata, int datalen, void* puser);

//设备数据变更回调,用于接收数据变更通知
typedef struct
{
	void*			   puser;//用户指针 回调函数中会通过 void* puser 返回该指针
	meetPB_callback    pfunc;//回调函数
	MEETCORE_LOGCB plogfunc;//日志回调函数
}MeetPBCore_CallBack, *pMeetPBCore_CallBack;
void SetmeetPB_callbackFunction(pMeetPBCore_CallBack pa);

//功能接口调用
/*
type: 操作类型
method:操作方法
pdata：类型、方法对应的数据
datalen：数据的大小 字节
pretdata：类型、方法对应的返回数据 需要使用 meetPB_free 释放资源 
retdatalen：返回的数据的大小 字节
*/
int meetPB_call(int32u type, int32u method, void* pdata, int datalen, void** pretdata, int* retdatalen, int32u operflag);

//释放功能接口调用获取到的数据
/*
pretdata：类型、方法对应的返回数据
retdatalen：返回的数据的大小 字节
*/
void meetPB_free(void* pretdata, int retdatalen);


#endif
