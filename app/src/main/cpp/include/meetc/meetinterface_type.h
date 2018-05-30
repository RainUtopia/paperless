#ifndef MEETINTERFACE_TYPE_H
#define MEETINTERFACE_TYPE_H

#include "base.h"
#include "interpublic.h"
#include "owbase.h"
#include "commdef.h"
#pragma pack(push, 4)

// #ifdef __cplusplus
// extern "C" {
// #endif

//triggeruserval
#define EXCEC_USERDEF_FLAG_FORCEOPEN					0x00000001 //该位为1表示强制推送,为0表示询问
#define MEETFILE_PUSH_FLAG_FORCEMODE					0x00000002 //该位为1表示强制模式 不允许自己停止,为0表示自由模式
#define EXCEC_USERDEF_FLAG_NOCREATEWINOPER				0x00000004 //不创建新窗口操作标志
#define EXCEC_USERDEF_FLAG_NOSTREAMRECORD				0x00000008 //不保存流录音操作标志
#define EXCEC_USERDEF_FLAG_SCREENSHARE					0x00000010 //同屏开启

//error
#define ERROR_MEET_INTERFACE_NULL	 0  //操作成功，或者数据为空，视操作请求定
#define ERROR_MEET_INTERFACE_NOTINIT -1 //未初始化
#define ERROR_MEET_INTERFACE_MEMORY  -2 //内存分配失败
#define ERROR_MEET_INTERFACE_NOSPPORTTYPE  -3 //不支持的请求
#define ERROR_MEET_INTERFACE_NOSPPORTMETHOD  -4 //不支持的请求方法
#define ERROR_MEET_INTERFACE_DOWNLOAD  -5 //下载失败
#define ERROR_MEET_INTERFACE_NETSEND -6 //发送失败
#define ERROR_MEET_INTERFACE_NOREADY -7 //数据未准备好,可能正在等待数据返回
#define ERROR_MEET_INTERFACE_NOFIND -8 //找不到
#define ERROR_MEET_INTERFACE_NOSPPORTPROPERTY -9 //不支持的属性
#define ERROR_MEET_INTERFACE_UPPONMAXVALUE -10 //超过最大值
#define ERROR_MEET_INTERFACE_ZERO -11 //个数为0
#define ERROR_MEET_INTERFACE_PARAMETER -12 //参数错误
#define ERROR_MEET_INTERFACE_DATA -13 //数据错误

//method
#define METHOD_MEET_INTERFACE_QUERY     1
#define METHOD_MEET_INTERFACE_ADD		2
#define METHOD_MEET_INTERFACE_MODIFY    3
#define METHOD_MEET_INTERFACE_DEL		4
#define METHOD_MEET_INTERFACE_SET		6
#define METHOD_MEET_INTERFACE_GETSIZE   7
#define METHOD_MEET_INTERFACE_NOTIFY    8
#define METHOD_MEET_INTERFACE_CLEAR     9
#define METHOD_MEET_INTERFACE_LOGON     10
#define METHOD_MEET_INTERFACE_PUSH      11
#define METHOD_MEET_INTERFACE_ASK       12
#define METHOD_MEET_INTERFACE_REJECT    13
#define METHOD_MEET_INTERFACE_EXIT      14
#define METHOD_MEET_INTERFACE_OPEN      15
#define METHOD_MEET_INTERFACE_CLOSE     16
#define METHOD_MEET_INTERFACE_ENTER     17
#define METHOD_MEET_INTERFACE_PAGEQUERY     18
#define METHOD_MEET_INTERFACE_MODIFYNAME    19
#define METHOD_MEET_INTERFACE_MODIFYINFO    20
#define METHOD_MEET_INTERFACE_MODIFYSTATUS    21
#define METHOD_MEET_INTERFACE_SINGLEQUERYBYID     22
#define METHOD_MEET_INTERFACE_SINGLEQUERYBYNAME   23
#define METHOD_MEET_INTERFACE_CONTROL   24
#define METHOD_MEET_INTERFACE_SAVE   25
#define METHOD_MEET_INTERFACE_REFRESH   26
#define METHOD_MEET_INTERFACE_COMPLEXQUERY     27 //复合查询
#define METHOD_MEET_INTERFACE_COMPLEXPAGEQUERY     28 //复合按页查询
#define METHOD_MEET_INTERFACE_START   29
#define METHOD_MEET_INTERFACE_STOP   30
#define METHOD_MEET_INTERFACE_PLAY   31
#define METHOD_MEET_INTERFACE_PAUSE   32
#define METHOD_MEET_INTERFACE_MOVE   33
#define METHOD_MEET_INTERFACE_ZOOM   34 //缩放操作 
#define METHOD_MEET_INTERFACE_BACK   35 //
#define METHOD_MEET_INTERFACE_REQUEST   36 //
#define METHOD_MEET_INTERFACE_BROADCAST   37 //
#define METHOD_MEET_INTERFACE_QUERYPROPERTY   38 //查询属性
#define METHOD_MEET_INTERFACE_SETPROPERTY   39 //设置属性
#define METHOD_MEET_INTERFACE_DUMP   40 //复制
#define METHOD_MEET_INTERFACE_REQUESTPUSH   41 //询问推送
#define METHOD_MEET_INTERFACE_DELALL		42
#define METHOD_MEET_INTERFACE_SEND		43
#define METHOD_MEET_INTERFACE_DETAILINFO		44
#define METHOD_MEET_INTERFACE_INIT		45
#define METHOD_MEET_INTERFACE_DESTORY		46
#define METHOD_MEET_INTERFACE_MAKEVIDEO		47
#define METHOD_MEET_INTERFACE_SUBMIT		48
#define METHOD_MEET_INTERFACE_PUBLIST		49
#define METHOD_MEET_INTERFACE_CACHE		50 //缓存
#define METHOD_MEET_INTERFACE_CHILDRED		51 //子级数据
#define METHOD_MEET_INTERFACE_ALLCHILDRED		52 //所有子级数据
#define METHOD_MEET_INTERFACE_RESPONSE   53 //
#define METHOD_MEET_INTERFACE_REQUESTTOMANAGE   54 //
#define METHOD_MEET_INTERFACE_RESPONSETOMANAGE   55 //
#define METHOD_MEET_INTERFACE_REQUESTPRIVELIGE   56 //
#define METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE   57 //
#define METHOD_MEET_INTERFACE_UPDATE   58 //
#define METHOD_MEET_INTERFACE_TEXTMSG   59 //
#define METHOD_MEET_INTERFACE_REBOOT    60 //
#define METHOD_MEET_INTERFACE_RESINFO   61 //

//type
#define TYPE_MEET_INTERFACE_TIME 1 //平台时间 -- 高频回调
#define TYPE_MEET_INTERFACE_READY 2 //平台初始化完毕
#define TYPE_MEET_INTERFACE_DOWNLOAD 3 //平台下载 -- 高频回调
#define TYPE_MEET_INTERFACE_UPLOAD 4 //平台上传 -- 高频回调
#define TYPE_MEET_INTERFACE_MEDIAPLAYPOSINFO 5 //平台播放进度通知 -- 高频回调
#define TYPE_MEET_INTERFACE_DEVICEINFO 6 //设备
#define TYPE_MEET_INTERFACE_DEFAULTURL 7 //会议网页
#define TYPE_MEET_INTERFACE_ADMIN	 8 //管理员
#define TYPE_MEET_INTERFACE_DEVICECONTROL 9 //设备控制
#define TYPE_MEET_INTERFACE_PEOPLE  10 //常用人员信息
#define TYPE_MEET_INTERFACE_MEMBER  11 //参会人员信息
#define TYPE_MEET_INTERFACE_MEMBERGROUP  12 //参会人员分组
#define TYPE_MEET_INTERFACE_MEMBERGROUPITEM  13 //参会人员分组人员
#define TYPE_MEET_INTERFACE_DEVICEFACESHOW  14 //设备会议信息
#define TYPE_MEET_INTERFACE_ROOM  15 //会场
#define TYPE_MEET_INTERFACE_ROOMDEVICE  16 //会场设备
#define TYPE_MEET_INTERFACE_FILEPUSH  17 //文件推送
#define TYPE_MEET_INTERFACE_REQUESTSTREAMPUSH  18 //流请求推送
#define TYPE_MEET_INTERFACE_STREAMPUSH  19 //流推送
#define TYPE_MEET_INTERFACE_MEETINFO  20 //会议信息
#define TYPE_MEET_INTERFACE_MEETAGENDA  21 //会议议程
#define TYPE_MEET_INTERFACE_MEETBULLET  22 //会议公告
#define TYPE_MEET_INTERFACE_MEETDIRECTORY  23 //会议目录
#define TYPE_MEET_INTERFACE_MEETDIRECTORYFILE  24 //会议目录文件
#define TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT  25 //会议目录权限
#define TYPE_MEET_INTERFACE_MEETVIDEO  26 //会议视频
#define TYPE_MEET_INTERFACE_MEETTABLECARD  27 //会议双屏显示
#define TYPE_MEET_INTERFACE_MEETSEAT  28 //会议排位
#define TYPE_MEET_INTERFACE_MEETIM  29 //会议交流
#define TYPE_MEET_INTERFACE_MEETONVOTING  30 //会议发起投票
#define TYPE_MEET_INTERFACE_MEETVOTEINFO 31 //会议投票信息
#define TYPE_MEET_INTERFACE_MEETVOTESIGNED 32 //会议投票人信息
#define TYPE_MEET_INTERFACE_MANAGEROOM 33 //会议管理会场
#define TYPE_MEET_INTERFACE_MEETSIGN 34 //会议签到
#define TYPE_MEET_INTERFACE_WHITEBOARD 35 //会议白板
#define TYPE_MEET_INTERFACE_FUNCONFIG  36 //会议功能配置
#define TYPE_MEET_INTERFACE_MEMBERPERMISSION  37 //参会人员权限信息
#define TYPE_MEET_INTERFACE_PEOPLEGROUP  38 //常用人员分组
#define TYPE_MEET_INTERFACE_PEOPLEGROUPITEM  39 //常用人员分组人员
#define TYPE_MEET_INTERFACE_SCREENMOUSECONTROL  40 //屏幕鼠标控制
#define TYPE_MEET_INTERFACE_SCREENKEYBOARDCONTROL  41 //屏幕键盘控制
#define TYPE_MEET_INTERFACE_DEVICEMEETSTATUS  42 //设备当前会议的一些信息
#define TYPE_MEET_INTERFACE_DEVICEOPER  43 //设备交互信息
#define TYPE_MEET_INTERFACE_DBSERVERERROR 44 //数据库后台回复错误信息
#define TYPE_MEET_INTERFACE_MEDIAPLAY  45 //媒体播放
#define TYPE_MEET_INTERFACE_STREAMPLAY 46 //流播放
#define TYPE_MEET_INTERFACE_STOPPLAY 47 //停止播放
#define TYPE_MEET_INTERFACE_MEMBERCOLOR  48 //参会人员白板颜色
#define TYPE_MEET_INTERFACE_MEETCONTEXT  49 //上下文
#define TYPE_MEET_INTERFACE_MEETCLEAR  50 //缓存清理,清空所有缓存,其它类型也支持单独
#define TYPE_MEET_INTERFACE_MEETSTATISTIC  51 //会议统计
#define TYPE_MEET_INTERFACE_MEETFACECONFIG  52 //界面配置
#define TYPE_MEET_INTERFACE_MEETSENDMAIL  53 //邮件发送
#define TYPE_MEET_INTERFACE_FILESCORE     54    //会议文件评分相关
#define TYPE_MEET_INTERFACE_FILEEVALUATE  55  //会议文件评价相关
#define TYPE_MEET_INTERFACE_MEETEVALUATE  56   //会议评价相关
#define TYPE_MEET_INTERFACE_SYSTEMLOG     57 //管理员操作日志相关
#define TYPE_MEET_INTERFACE_DEVICEVALIDATE 58 //设备ID校验
#define TYPE_MEET_INTERFACE_SYSTEMFUNCTIONLIMIT 59 //平台功能限制

//注：这里所有的字符串都约定为utf8编码
#define DEFAULT_NAME_MAXLEN   48 //默认名称长度
#define DEFAULT_VOTECONTENT_LENG 200//默认投票标题描述字符串长度
#define DEFAULT_VOTEITEM_LENG    60//默认投票选项描述字符串长度
#define DEFAULT_DESCRIBE_LENG    100//默认描述字符串长度
#define DEFAULT_SHORT_DESCRIBE_LENG 40//默认描述字符串长度
#define DEFAULT_PASSWORD_LENG 40 //默认密码长度
#define DEFAULT_FILENAME_LENG 150 //默认文件名长度

//font align flag 字体对齐方式
#define MEET_FONTFLAG_LEFT			0x0001 //左对齐
#define MEET_FONTFLAG_RIGHT			0x0002 //右对齐
#define MEET_FONTFLAG_HCENTER		0x0004 //水平对齐
#define MEET_FONTFLAG_TOP			0x0008 //上对齐
#define MEET_FONTFLAG_BOTTOM		0x0010 //下对齐
#define MEET_FONTFLAG_VCENTER		0x0020 //垂直对齐

//////////////////////////////////////////////////////////////////////////
typedef struct
{
	int32u totalbufsize;//数据所用的总内存大小
	int32u type;//请求类型
	int32u method;//请求方法
}Type_HeaderInfo, *pType_HeaderInfo;

//平台网络和数据初始化成功
//type:TYPE_MEET_INTERFACE_READY
//method: notify
//callback
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			areaid;//连接上的区域服务器ID
}Type_Ready, *pType_Ready;

//平台设备校验
//type:TYPE_MEET_INTERFACE_DEVICEVALIDATE
//method: notify
//callback
typedef struct
{
	Type_HeaderInfo hdr;

	//参考basemacro.h的宏定义
	int32u      valflag;
	int32u      val[32];
	int64u		user64bitdef[2];
}Type_DeviceValidate, *pType_DeviceValidate;

#define SYSTEMFUNCTIONLIMIT_PROPERTY_ISENABLE 0 //查询功能是否可用 query,parameter(传入的功能ID 参见systemfuncion.h的功能ID定义),propertyval(返回是否可用1=可用,0=不可用)
#define SYSTEMFUNCTIONLIMIT_PROPERTY_TOTALNUM 1 //查询总数 query,parameter(0),returnval(返回总数)

//平台功能限制
//type:TYPE_MEET_INTERFACE_SYSTEMFUNCTIONLIMIT
//method: queryproperty
//call
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;
	int32u parameter;
	int32u propertyval;
}Type_SystemFuntionLimitsProperty, *pType_SystemFuntionLimitsProperty;

//平台功能限制
//type:TYPE_MEET_INTERFACE_SYSTEMFUNCTIONLIMIT
//method: query queryproperty
//call
typedef struct
{
	Type_HeaderInfo hdr;
	
	int      num;
	//int32u      functionids[num];//参见systemfuncion.h的功能ID定义
}Type_SystemFuntionLimits, *pType_SystemFuntionLimits;

//平台时间
//type:TYPE_MEET_INTERFACE_TIME
//method: notify
//callback
typedef struct
{
	Type_HeaderInfo hdr;
	int64u			usec;//本地时间，每秒一次 单位：微秒
}Type_Time, *pType_Time;

//缓存数据
#define MEET_CACEH_FLAG_FORCE	     0x00000001 //强制缓存

//call
//type:需要缓存数据的类别
//method: cache 
//如果有缓存则返回ERROR_MEET_INTERFACE_NULL
typedef struct
{
	Type_HeaderInfo hdr;
	int32u      cacheflag;//

	//id用法描述 eg:需要缓存目录id=0表示缓存所有目录信息(不包括目录里的文件),当id=1时表示缓存该目录里的文件,如果id=0不支持则会返回ERROR_MEET_INTERFACE_PARAMETER
	int32u		id;
}Type_MeetCacheOper, *pType_MeetCacheOper;

//分页查询
//call
//type:需要查询的数据的类别
//method: pagequery 应用方调用
typedef struct
{
	Type_HeaderInfo hdr;
	int			pageindex;//请求页 从0 开始
	int			pagenum;  //每页项数
	int32u      idval;//用于分组使用,如：查询指分组中的人员,这里填写分组id
}TypePageReqQueryInfo, *pTypePageReqQueryInfo;

//分页查询返回结构头部
//callreturn
//type:需要查询的数据的类别
//method: pagequery 接口方返回
typedef struct
{
	Type_HeaderInfo hdr;
	int			pageindex;//起始索引 从0 开始
	int			pagenum;//每页项数
	int			itemnum;//当前页项数 ,根据数据不同用不同的解析
	int			totalnum;//总项数
	int32u      idval;//用于分组使用,如：查询指分组中的人员,这里填写分组id
}TypePageResQueryInfo, *pTypePageResQueryInfo;

//指定ID查询 返回结果按 query 返回
//call
//type:需要查询的数据的类别
//method: SINGLEQUERYBYID 应用方调用
typedef struct
{
	Type_HeaderInfo hdr;
	int32u id;
}TypeQueryInfoByID, *pTypeQueryInfoByID;

//指定名称查询 返回结果按 query 返回
//method: SINGLEQUERYBYNAME 应用方调用
typedef struct
{
	Type_HeaderInfo hdr;
	char name[DEFAULT_FILENAME_LENG];
}TypeQueryInfoByName, *pTypeQueryInfoByName;

//指定双重ID查询 返回结果按 query 返回
//method: SINGLEQUERYBYID 应用方调用
typedef struct
{
	Type_HeaderInfo hdr;
	int32u id1;
	int32u id2;
}TypeQueryInfoByDoubleID, *pTypeQueryInfoByDoubleID;

//指定双重ID查询 返回结果按 query 返回
//method: SINGLEQUERYBYNAME 应用方调用
typedef struct
{
	Type_HeaderInfo hdr;
	int32u id1;
	char name[DEFAULT_FILENAME_LENG];
}TypeDoubleQueryInfoByName, *pTypeDoubleQueryInfoByName;

//获取大小
//callreturn
//type:需要查询的大小的类别
//method: METHOD_MEET_INTERFACE_GETSIZE
typedef struct
{
	Type_HeaderInfo hdr;
	int32u      idval;//用于分组使用,如：查询指分组中的人员,这里填写分组id

	int32u		size;//返回的大小
}Type_MeetQuerySize, *pType_MeetQuerySize;

//////////////////////////////////////////////////////////////////////////
//设备的基本信息
//callback
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  deviceid;
	int		attribid;// 0:net status  50:res status  63:base info
}Type_MeetDeviceBaseInfo, *pType_MeetDeviceBaseInfo;

//MemState 设备当前所处的界面状态
#define MemState_MainFace  0 //处于主界面
#define MemState_MemFace   1 //参会人员界面
#define MemState_AdminFace 2 //后台管理界面
#define MemState_VoteFace  3 //投票界面

//平台设备
//playstatus
#define DEVICE_PLAYSTATUS_IDLE 0
#define DEVICE_PLAYSTATUS_MEDIA 1
#define DEVICE_PLAYSTATUS_STREAM 2
#define DEVICE_PLAYSTATUS_TALK 3

#define DEVICE_NAME_MAXLEN   128
#define DEVICE_IPADDR_MAXLEN 64

#define MAX_DEVICEIPADDR_NUM 2 //设备最大的IP地址信息数
typedef struct
{
	char   ip[DEVICE_IPADDR_MAXLEN];
	int16u port;
}SubItem_DeviceIpAddrInfo, *pSubItem_DeviceIpAddrInfo;

typedef struct
{
	int8u  playstatus;
	int32u triggerId;
	int32u val; //status =DEVICE_PLAYSTATUS_MEDIA mediaid，DEVICE_PLAYSTATUS_STREAM设备ID，DEVICE_PLAYSTATUS_TALK对讲设备ID
	int32u val2; //status =DEVICE_PLAYSTATUS_MEDIA 0播放中,1暂停|DEVICE_PLAYSTATUS_STREAM,DEVICE_PLAYSTATUS_TALK子流通道
}SubItem_DeviceResInfo, *pSubItem_DeviceResInfo;
typedef struct
{
	int32u devcieid;
	char   devname[DEVICE_NAME_MAXLEN];
	int32u netstate;

	SubItem_DeviceIpAddrInfo ipinfo[MAX_DEVICEIPADDR_NUM];
	SubItem_DeviceResInfo    resinfo[MAX_RES_NUM];

	int32u	facestate;//界面状态 参见MemState_MainFace 定义
	int32u  memberid;//当前人员
	int32u  meetingid;//当前会议
	int8u   liftgroupres[2];//0升降机组ID 1升降话筒组ID
}Item_DeviceDetailInfo, *pItem_DeviceDetailInfo;

//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			devnum;
}Type_DeviceDetailInfo, *pType_DeviceDetailInfo;

typedef struct
{
	int32u devceid;

	//如果设备ID是参会人员席位,以下数据有效
	int32u memberid;
	char   name[DEFAULT_NAME_MAXLEN];
}Item_DeviceResPlay, *pItem_DeviceResPlay;

//查询当前可加入播放的设备流
//call
//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: METHOD_MEET_INTERFACE_RESINFO
typedef struct
{
	Type_HeaderInfo hdr;

	int32u			devnum;
	
}Type_DeviceResPlay, *pType_DeviceResPlay;

//查询指定设备的某项属性
//property id
#define MEETDEVICE_PROPERTY_NAME				1 //设备名称 query
#define MEETDEVICE_PROPERTY_IPADDR				2 //ip地址 query paramterval(ip地址索引)
#define MEETDEVICE_PROPERTY_PORT				3 //网络端口 query
#define MEETDEVICE_PROPERTY_NETSTATUS			4 //网络状态 query
#define MEETDEVICE_PROPERTY_PLAYSTATUS			5 //播放状态 paramterval(res地址索引)(propertyval2(triggerid), propertyval3(val),propertyval4(val2),propertyval4(createdeviceid)有效) query
#define MEETDEVICE_PROPERTY_FACESTATUS			6 //界面状态 query
#define MEETDEVICE_PROPERTY_MEMBERID			7 //当前参会人员ID query
#define MEETDEVICE_PROPERTY_MEETINGID			8 //当前会议ID query
#define MEETDEVICE_PROPERTY_TRIGGERID			9 //当前正在执行的触发器ID query paramterval(res地址索引)
#define MEETDEVICE_PROPERTY_STREAMNAME			10 //设备的流通道名称 query paramterval=查询的流通道号
#define MEETDEVICE_PROPERTY_RESOPERTORID		11 //获取资源操作的设备ID query paramterval(res地址索引)
#define MEETDEVICE_PROPERTY_TYPEAVAILABLE		12 //某类设备是否可用 query deviceid(设备类别ID,eg:DEVICE_MEET_SERVICE) propertyval=1可用，=0不可用

#define MEET_DEVICESTRING_MAXLEN 260
//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u deviceid;//传入参数 为0表示本机设备ID
	int32u paramterval;//传入参数

	int32u propertyval; //数据 //0空闲，1播放节目，2播放流，3对讲
	int32u propertyval2;//数据
	int32u propertyval3;//数据
	int32u propertyval4;//数据
	int32u propertyval5;//数据
}Type_MeetDeviceQueryProperty_int32u, *pType_MeetDeviceQueryProperty_int32u;

//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u deviceid;//传入参数 为0表示本机设备ID
	int32u paramterval;//传入参数
	char   propertytext[MEET_DEVICESTRING_MAXLEN];//字符串

}Type_MeetDeviceQueryProperty_string, *pType_MeetDeviceQueryProperty_string;

//会议界面状态通知
//type:TYPE_MEET_INTERFACE_DEVICEMEETSTATUS
//method: qeuery/notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  deviceid;
	int32u  memberid;
	int32u  facestatus; // 参见MemState_MainFace 定义
	int32u  meetingid;
}Type_MeetDeviceMeetStatus, *pType_MeetDeviceMeetStatus;

//修改本机的会议界面状态
//type:TYPE_MEET_INTERFACE_DEVICEMEETSTATUS
//method: qeuery/modify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  facestatus; // 参见MemState_MainFace 定义
}Type_MeetModDeviceMeetStatus, *pType_MeetModDeviceMeetStatus;

//刷新设备的会议信息\广播当前会议参会人数量的信息
//call
//type:TYPE_MEET_INTERFACE_DEVICEMEETSTATUS
//method: refresh/broadcast
typedef struct
{
	Type_HeaderInfo hdr;
}Type_DeviceRefreshInfo, *pType_DeviceRefreshInfo;

//modflag
#define DEVICE_MODIFYFLAG_NAME		0x00000001 //表示修改名称
#define DEVICE_MODIFYFLAG_IPADDR	0x00000002 //表示修改ip地址
#define DEVICE_MODIFYFLAG_PORT		0x00000004 //表示修改端口
#define DEVICE_MODIFYFLAG_LIFTRES   0x00000008 //表示修改升降代码

//修改设备信息
//call
//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: modifyinfo
typedef struct
{
	Type_HeaderInfo hdr;

	int32u modflag;//指定需要修改的标志位

	int32u devcieid;
	char   devname[DEVICE_NAME_MAXLEN];
	SubItem_DeviceIpAddrInfo ipinfo[MAX_DEVICEIPADDR_NUM];
	int8u  liftgroupres[2];//0升降机组ID 1升降话筒组ID
}Type_DeviceModInfo, *pType_DeviceModInfo;

//删除设备
//call
//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u			devnum;
	//int32u devid[devnum];
}Type_DeviceDel, *pType_DeviceDel;

//升级设备
//call
//type:TYPE_MEET_INTERFACE_DEVICEINFO
//method: update
typedef struct
{
	Type_HeaderInfo hdr;
	int32u		mediaid;//已经上传完成的升级文件ID
}Type_DoDeviceUpdate, *pType_DoDeviceUpdate;

//窗口缩放
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: zoom
typedef struct
{
	Type_HeaderInfo hdr;

	int   zoomper;

	int   avalableres;//有效资源个数,为0表示全部
	int8u res[32];
}Type_MeetZoomResWin, *pType_MeetZoomResWin;

//窗口缩放
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: zoom
typedef struct
{
	Type_HeaderInfo hdr;

	int   zoomper;

	int   avalableres;//有效资源个数,为0表示全部
	int8u res[32];

	int   devnum;
	//int32u deviceid[devnum];
}Type_MeetDoZoomResWin, *pType_MeetDoZoomResWin;

//接到参会人员数量和签到数量的通知
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_BROADCAST
typedef struct
{
	Type_HeaderInfo hdr;

	int32u deviceid;//广播者设备ID
	int32u memberid;//广播者参会人员ID

	int32u membersize; //会议中广播时的参会人员数量
	int32u membersignsize;////会议中广播时的参会人员已经签到的数量
}Type_MeetMemberCastInfo, *pType_MeetMemberCastInfo;

//发送网络唤醒设备
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: reboot
typedef struct
{
	Type_HeaderInfo hdr;

	int   devnum;
	//int32u deviceid[devnum];
}Type_MeetDoNetReboot, *pType_MeetDoNetReboot;

//进入会议功能界面
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: enter
typedef struct
{
	Type_HeaderInfo hdr;
}Type_MeetEnterMeet, *pType_MeetEnterMeet;

//进入会议功能界面
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: enter
typedef struct
{
	Type_HeaderInfo hdr;

	int   devnum;
	//int32u deviceid[devnum];
}Type_MeetDoEnterMeet, *pType_MeetDoEnterMeet;

//回到主界面
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: back
typedef struct
{
	Type_HeaderInfo hdr;
}Type_MeetToHomePage, *pType_MeetToHomePage;

//回到主界面
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: back
typedef struct
{
	Type_HeaderInfo hdr;

	int   devnum;
	//int32u deviceid[devnum];
}Type_MeetDoToHomePage, *pType_MeetDoToHomePage;

//发送请求成为管理员
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_REQUESTTOMANAGE
typedef struct
{
	Type_HeaderInfo hdr;

	//指定收到该请求的设备ID
	int     devnum;
	//int32u deviceid[devnum];

}Type_MeetRequestManage, *pType_MeetRequestManage;

//收到请求成为管理员请求
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_REQUESTTOMANAGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID

}Type_MeetRequestManageNotify, *pType_MeetRequestManageNotify;

//回复请求成为管理员请求
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_RESPONSETOMANAGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  returncode;		//1=同意,0=不同意

	//指定收到该请求的设备ID
	int     devnum;
	//int32u deviceid[devnum];

}Type_MeetResponseRequestManage, *pType_MeetResponseRequestManage;

//收到请求成为管理员回复
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_RESPONSETOMANAGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID
	int32u  returncode;		//1=同意,0=不同意
}Type_MeetRequestManageResponse, *pType_MeetRequestManageResponse;

//发送请求参会人员权限请求
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_REQUESTPRIVELIGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  privilege;//申请的权限 见本文件 参会人权限 宏定义

	//指定收到该请求的设备ID
	int     devnum;
	//int32u deviceid[devnum];

}Type_MeetRequestPrivilege, *pType_MeetRequestPrivilege;

//收到请求参会人员权限请求
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_REQUESTPRIVELIGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID
	int32u  privilege;		//申请的权限 见本文件 参会人权限 宏定义
}Type_MeetRequestPrivilegeNotify, *pType_MeetRequestPrivilegeNotify;

//回复参会人员权限请求
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  privilege;		//回复申请的权限 见本文件 参会人权限 宏定义

	//指定收到该请求的设备ID
	int     devnum;
	//int32u deviceid[devnum];

}Type_MeetResponseRequestPrivilege, *pType_MeetResponseRequestPrivilege;

//收到参会人员权限请求回复
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID
	int32u  privilege;		//回复申请的权限 见本文件 参会人权限 宏定义
}Type_MeetRequestPrivilegeResponse, *pType_MeetRequestPrivilegeResponse;

//texttype
#define TEXTBRODCAST_TYPE_COMMONTEXT 0 //普通文本信息
#define TEXTBRODCAST_TYPE_MEETINGTEXT 1 //会议名称文本信息
#define TEXTBRODCAST_TYPE_MEMBERTEXT 2 //参会人文本信息

//发送文本广播
//call
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_TEXTMSG
typedef struct
{
	Type_HeaderInfo hdr;

	//指定收到该请求的设备ID
	int     devnum;
	//int32u deviceid[devnum];

	int32u  texttype;//文本类型
	char*   ptextmsg;//文本信息
}Type_MeetDoTextBrodcast, *pType_MeetDoTextBrodcast;

//收到文本广播
//callback
//type:TYPE_MEET_INTERFACE_DEVICEOPER
//method: METHOD_MEET_INTERFACE_TEXTMSG
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  texttype;//文本类型
	char*   ptext;//文本信息
}Type_MeetTextBrodcast, *pType_MeetTextBrodcast;

//会议上下文  查询整形数据

//property id
#define MEETCONTEXT_PROPERTY_ROLE				1 //当前会议的角色 set/query(int32u)
#define MEETCONTEXT_PROPERTY_CURADMINID			2 //当前会议登陆的管理员ID query(int32u)
#define MEETCONTEXT_PROPERTY_CURADMINNAME		3 //当前会议登陆的管理员用户名 query(string)
#define MEETCONTEXT_PROPERTY_CURADMINPASSWORD	4 //当前会议登陆的管理员密码 md5 query(string)
#define MEETCONTEXT_PROPERTY_HASTABLECARD		5 //当前会议是否是双屏 set/query(int32u)
#define MEETCONTEXT_PROPERTY_CURMEETINGID		6 //当前会议的会议ID set/query(int32u)
#define MEETCONTEXT_PROPERTY_SELFID				7 //本机设备ID query(int32u)
#define MEETCONTEXT_PROPERTY_CURROOMID			8 //当前会议的会场ID query(int32u)
#define MEETCONTEXT_PROPERTY_SELFMEMBERID		9 //当前会议的绑定的参会人员ID query(int32u)
#define MEETCONTEXT_PROPERTY_MEETDBDEVICEID		10 //当前会议数据库设备ID query(int32u)
#define MEETCONTEXT_PROPERTY_SCREENSTREAMINDEX	11 //本机屏幕流通道号 query(int32u)
#define MEETCONTEXT_PROPERTY_AVAILABLEMEDIASERER	12 //媒体服务器是否可用 query propertyval=返回可用的个数(int32u)
#define MEETCONTEXT_PROPERTY_AVAILABLESTREAMSERVER	13 //流服务器是否可用 query=返回可用的个数(int32u)
#define MEETCONTEXT_PROPERTY_UTCMICROSECONDS	14 //返回当前系统微秒UTC时间 query(int64u)
#define MEETCONTEXT_PROPERTY_LOCALCICROSECONDS	15 //返回当前系统微秒时间 query(int64u)

//type: TYPE_MEET_INTERFACE_MEETCONTEXT
//method: queryproperty/setproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u propertyval;//值 
}Type_MeetContextInfo_int32u, *pType_MeetContextInfo_int32u;

//type: TYPE_MEET_INTERFACE_MEETCONTEXT
//method: queryproperty/setproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int64u propertyval;//值 
}Type_MeetContextInfo_int64u, *pType_MeetContextInfo_int64u;

//会议上下文 查询字符串
#define MEET_CONTEXTSTRING_MAXLEN 260
//type: TYPE_MEET_INTERFACE_MEETCONTEXT
//method: query /set
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	char   propertytext[MEET_CONTEXTSTRING_MAXLEN];//字符串
}Type_MeetContextInfo_string, *pType_MeetContextInfo_string;

//会议网页
#define MEET_URL_MAXLEN 1024
//type: TYPE_MEET_INTERFACE_DEFAULTURL
//method: notify,modify,query
typedef struct
{
	Type_HeaderInfo hdr;
	char url[MEET_URL_MAXLEN];
}Type_meetUrl, *pType_meetUrl;

//会议议程
//type: TYPE_MEET_INTERFACE_MEETAGENDA
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetAgendaInfo, *pType_MeetAgendaInfo;

//会议议程
//agendatype
#define MEET_AGENDA_TYPE_TEXT 0 //文本 val=文本长度
#define MEET_AGENDA_TYPE_FILE 1 //文件 val=媒体ID
#define MEET_AGENDA_TYPE_TIME 2 //时间轴式 val=Item_AgendaTimeInfo个数

//时间轴式会议议程
#define MEETAGENDA_STATUS_IDLE		0 //未发起
#define MEETAGENDA_STATUS_RUNNING   1 //进行中
#define MEETAGENDA_STATUS_END		2 //已结束

#define MEETAGENDA_DESCTEXT_LENG    320
typedef struct
{
	int32u  agendaid;  //议程ID
	int32u  status;  //议程状态
	int32u  dirid;  //绑定目录ID
	int64u startutctime;//单位秒
	int64u endutctime;  //单位秒
	char desctext[MEETAGENDA_DESCTEXT_LENG]; //描述内容
}Item_AgendaTimeInfo, *pItem_AgendaTimeInfo;

//type: TYPE_MEET_INTERFACE_MEETAGENDA
//method: (MEET_AGENDA_TYPE_TEXT|MEET_AGENDA_TYPE_FILE:modify,query)（MEET_AGENDA_TYPE_TIME:modify,query,add,del,set）
typedef struct
{
	Type_HeaderInfo hdr;
	
	int32u	 agendatype;//议程类型
	int32u   val;// 
}Type_meetAgenda, *pType_meetAgenda;

//会议公告
//TYPE_MEET_INTERFACE_MEETBULLET
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetBulletInfo, *pType_MeetBulletInfo;

//TYPE_MEET_INTERFACE_MEETBULLET
//call
//method: request,update
typedef struct
{
	Type_HeaderInfo hdr;

	int textlen;
	//char text[textlen];
}Type_BigBulletDetailInfo, *pType_BigBulletDetailInfo;

//会议公告
#define DEFAULT_BULLETCONTENT_LENG 320
typedef struct
{
	int32u		bulletid;
	char	    title[DEFAULT_DESCRIBE_LENG]; //标题
	char	    content[DEFAULT_BULLETCONTENT_LENG]; //内容 
	int32u		type;//类别
	int32u		starttime; //开始时间
	int32u		timeouts;  //超时值
}Item_BulletDetailInfo, *pItem_BulletDetailInfo;

//TYPE_MEET_INTERFACE_MEETBULLET
//call
//method: add,del,modify,query
typedef struct
{
	Type_HeaderInfo hdr;

	int num;
}Type_BulletDetailInfo, *pType_BulletDetailInfo;

//会议公告发布
//call callback
//TYPE_MEET_INTERFACE_MEETBULLET
//method: publish
typedef struct
{
	Type_HeaderInfo hdr;
	Item_BulletDetailInfo bullet;
	int devnum; //发给那个设备
	//int32u deviceid[];
}Type_MeetPublishBulletInfo, *pType_MeetPublishBulletInfo;

//停止会议公告
//TYPE_MEET_INTERFACE_MEETBULLET
//call
//method: stop
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  bulletid;//停止的会议公告ID
	int     devnum; //设备ID数量，为0表示所有的设备
	//int32u pdevid[];//设备ID
}Type_StopBullet, *pType_StopBullet;

//停止会议公告
//TYPE_MEET_INTERFACE_MEETBULLET
//callback
//method: stop
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  bulletid;//停止的会议公告ID
}Type_StopBulletMsg, *pType_StopBulletMsg;

//管理员
//callback
//TYPE_MEET_INTERFACE_ADMIN
//method:notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部
}Type_meetAdmin, *pType_meetAdmin;

typedef struct
{
	int32u adminid;
	char   adminname[DEFAULT_NAME_MAXLEN];
	char   pw[DEFAULT_PASSWORD_LENG];//密码 md5 ascill 32字节
	char   comment[DEFAULT_DESCRIBE_LENG];
	char   phone[DEFAULT_DESCRIBE_LENG];
	char   email[DEFAULT_DESCRIBE_LENG];
}Item_AdminDetailInfo, *pItem_AdminDetailInfo;

//TYPE_MEET_INTERFACE_ADMIN
//method: add\del\modify\query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			adminnum;
}TypeAdminDetailInfo, *pTypeAdminDetailInfo;

//管理员登陆
#define MEET_NAME_MAXLEN	   100
#define MEET_MD5_ASCILL_MAXLEN 32

//TYPE_MEET_INTERFACE_ADMIN
//method: set
typedef struct
{
	Type_HeaderInfo hdr;

	char			adminname[MEET_NAME_MAXLEN];//用户名
	char			adminoldpwd[MEET_MD5_ASCILL_MAXLEN];//用户密码(ascill/md5ascill)
	char			adminnewpwd[MEET_MD5_ASCILL_MAXLEN];//用户密码(ascill/md5ascill)
}Type_AdminModifyPwd, *pType_AdminModifyPwd;

//TYPE_MEET_INTERFACE_ADMIN
//method: logon
typedef struct
{
	Type_HeaderInfo hdr;

	int				isascill;//是否已经md5加密
	char			adminname[MEET_NAME_MAXLEN];//用户名
	char			adminpwd[MEET_MD5_ASCILL_MAXLEN];//用户密码(ascill/md5ascill)
}Type_AdminLogon, *pType_AdminLogon;

//登陆返回
#define ADMINLOGON_ERR_NONE 0 //登陆成功
#define ADMINLOGON_ERR_PSW  1 //密码错误
#define ADMINLOGON_ERR_EXCPT_SV  2 //服务器异常
#define ADMINLOGON_ERR_EXCPT_DB  3 //数据库异常

//TYPE_MEET_INTERFACE_ADMIN
//method: logon
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			err;//
	int32u			sessionid;//会话ID
	int32u			adminid;//会话ID
	char			adminname[MEET_NAME_MAXLEN];//会话ID
}Type_AdminLogonStatus, *pType_AdminLogonStatus;

//会议管理员控制的会场
//TYPE_MEET_INTERFACE_MANAGEROOM
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetManageRoom, *pType_MeetManageRoom;

//会议管理员控制的会场
typedef struct
{
	int32u		roomid; //会场ID
}Item_MeetManagerRoomDetailInfo, *pItem_MeetManagerRoomDetailInfo;

//type:TYPE_MEET_INTERFACE_MANAGEROOM
//method: query/save
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   mgrid;//管理员ID
	int32u	 num; //
}Type_MeetManagerRoomDetailInfo, *pType_MeetManagerRoomDetailInfo;

//终端控制
//oper
#define DEVICECONTORL_SHUTDOWN 1 //关机
#define DEVICECONTORL_REBOOT  2 //重启
#define DEVICECONTORL_PROGRAMRESTART  3 //重启软件
#define DEVICECONTORL_LIFTUP  4 //升
#define DEVICECONTORL_LIFTDOWN  5 //降
#define DEVICECONTORL_LIFTSTOP  6 //停止升（降）
#define DEVICECONTORL_MODIFYLOGO  7 //更换LOGO
#define DEVICECONTORL_MODIFYMAINBG  8 //更换主界面
#define DEVICECONTORL_MODIFYPROJECTBG  9 //更换投影界面
#define DEVICECONTORL_MODIFYSUBBG  10 //更换子界面
#define DEVICECONTORL_MODIFYFONTCOLOR  11 //更换字体颜色

//callback
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			oper;//
	int32u			operval1;//操作对应的参数 如更换主界面的媒体ID
	int32u			operval2;//操作对应的参数
}Type_DeviceControl, *pDeviceControl;

//operval1 升降控制类别标志
#define LIFT_FLAG_MACHICE 0x00000001 //选择升降机类别
#define LIFT_FLAG_MIC     0x00000002 //选择升降话筒类别

//TYPE_MEET_INTERFACE_DEVICEINFO
//method: control
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			oper;//
	int32u			operval1;//操作对应的参数 如更换主界面的媒体ID
	int32u			operval2;//操作对应的参数
	int32u			devnum; //控制的设备数 为0当前会议表示所有
}Type_DeviceOperControl, *pType_DeviceOperControl;

//下载
#define MEET_PATH_MAXLEN 1024
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u mediaid;					//下载媒体id
	int	   newfile;				    //设置是否重新下载,0 不覆盖同名文件,重新下载
	void*  puser;					//用户指针
	int	   onlyfinish;				//1表示只需要结束的通知
	char   pathname[MEET_PATH_MAXLEN];//下载媒体全路径名称
}Type_DownloadStart, *pType_DownloadStart;

//删除一个下载
//method: del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u mediaid;	//下载媒体id
}Type_DownloadDel, *pType_DownloadDel;

//清空下载
//method: clear
typedef struct
{
	Type_HeaderInfo hdr;
}Type_DownloadClear, *pType_DownloadClear;

//state flag
#define STATE_MEDIA_DOWNLOAD_IDLE	  0 
#define STATE_MEDIA_DOWNLOAD_WORKING  1 
#define STATE_MEDIA_DOWNLOAD_FINISH   2
#define STATE_MEDIA_DOWNLOAD_ERROR    3
#define STATE_MEDIA_DOWNLOAD_EXIT     4
#define STATE_MEDIA_DOWNLOAD_DESTROY  5

//ERROR 
#define ERROR_MEDIA_DOWNLOAD_OK			0 //正常
#define ERROR_MEDIA_DOWNLOAD_PARAMETER  1 //参数不对
#define ERROR_MEDIA_DOWNLOAD_INITTHREAD 2 //线程创建失败
#define ERROR_MEDIA_DOWNLOAD_USERSTOP	3 //主动停止
#define ERROR_MEDIA_DOWNLOAD_NOMEDIA	4 //不存在的媒体 
#define ERROR_MEDIA_DOWNLOAD_OPEN		5 //打开文件失败
#define ERROR_MEDIA_DOWNLOAD_FILEOK		6 //文件完整,不需要下载
#define ERROR_MEDIA_DOWNLOAD_CREATE		7 //创建 文件失败
#define ERROR_MEDIA_DOWNLOAD_MEMORY		8 //内存分配失败
#define ERROR_MEDIA_DOWNLOAD_EXECINIT	9 //平台库初始化下载失败
#define ERROR_MEDIA_DOWNLOAD_SETPOS		10 //设置下载位置失败

//下载进度通知
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int	   progress;				//下载进度
	int	   nstate;				    //下载状态,参考state flag
	int	   err;						//错误值 ERROR
	int32u mediaid;					//下载媒体id
	void*  puser;					//用户指针
}Type_DownloadCb, *pType_DownloadCb;

//播放进度通知
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u mediaId;						//媒体ID
	int8u  status;						//0=播放中，1=暂停，2=停止,3=恢复
	int8u  per;							//当前位置，百分比
	int32u sec;							//当前播放秒数，当status>0时，为文件ID号
}Type_PlayPosCb, *pType_PlayPosCb;

//status
#define UPLOADMEDIA_FLAG_IDLE		0 //等待上传
#define UPLOADMEDIA_FLAG_UPLOADING  1 //上传中
#define UPLOADMEDIA_FLAG_STOP	    2 //停止
#define UPLOADMEDIA_FLAG_HADEND		3 //结束上传
#define UPLOADMEDIA_FLAG_NOSERVER	4 //没找到可用的服务器
#define UPLOADMEDIA_FLAG_ISBEING	5 //已经存在
//上传进度通知
//callback
//type:TYPE_MEET_INTERFACE_UPLOAD
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u mediaId;						//媒体ID
	int8u  status;					
	int8u  per;							//当前位置，百分比
	int32u uploadflag;
	int32u userval;//
}TypeUploadPosCb, *pTypeUploadPosCb;

//上传一个文件
#define MEET_PATHNAME_MAXLEN 1024

//uploadflag
#define MEET_UPLOADFLAG_ONLYENDCALLBACK 0x00000001 //只能当上传结束才回调进度

//call
//系统保留使用的目录ID,用作特别用途
#define MEET_UPDATE_SYSTEMDIRID_CHANGELOGO			0xffffffff  //更换LOGO
#define MEET_UPDATE_SYSTEMDIRID_CHANGEMAINBG		0xfffffffe  //更换主界面
#define MEET_UPDATE_SYSTEMDIRID_CHANGEPROJECTIVEBG	0xfffffffd  //更换投影主界面
#define MEET_UPDATE_SYSTEMDIRID_END					0xfffffff0 
//type:TYPE_MEET_INTERFACE_UPLOAD
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u uploadflag;//上传标志
	int32u dirid;//上传的目录ID

	int32u attrib;//文件属性
	char   newname[DEFAULT_FILENAME_LENG];//上传后的新名称
	char   pathname[MEET_PATHNAME_MAXLEN];//全路径名

	int32u userval;//
	int32u mediaid;//上传时返回的媒体ID
}Type_AddUploadFile, *pType_AddUploadFile;

//查询上传文件
typedef struct
{
	int32u  mediaid;//媒体ID
	int32u  dirid;//上传到的目录ID

	int		errstatus;//错误码
	int32u  percent;//上传的百分比
	int32u  userval;//

	char   newname[DEFAULT_FILENAME_LENG];//上传后的新名称
	char   pathname[MEET_PATHNAME_MAXLEN];//全路径名
	
}Item_UploadFileDetailInfo, *pItem_UploadFileDetailInfo;

//call return
//method: query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			num;
}Type_UploadFileDetailInfo, *pType_UploadFileDetailInfo;

//删除上传
//call
//type:TYPE_MEET_INTERFACE_UPLOAD
//method: del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  mediaid;//媒体ID
}Type_DelUploadFile, *pType_DelUploadFile;

//常用人员
//callback
//TYPE_MEET_INTERFACE_PEOPLE
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_PeopleInfo, *pType_PeopleInfo;

//常用人员
typedef struct
{
	int32u personid;
	char   name[DEFAULT_NAME_MAXLEN];
	char   company[DEFAULT_DESCRIBE_LENG];
	char   job[DEFAULT_DESCRIBE_LENG];
	char   comment[DEFAULT_DESCRIBE_LENG];
	char   phone[DEFAULT_SHORT_DESCRIBE_LENG];
	char   email[DEFAULT_SHORT_DESCRIBE_LENG];
	char   password[DEFAULT_PASSWORD_LENG];
}Item_PersonDetailInfo, *pItem_PersonDetailInfo;

//call return
//TYPE_MEET_INTERFACE_PEOPLE
//method: add\del\modify\query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			personnum;
}Type_PersonDetailInfo, *pType_PersonDetailInfo;

//常用人员分组
//callback
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_PeopleGroup, *pType_PeopleGroup;

//常用人员分组
typedef struct
{
	int32u groupid;
	char   name[DEFAULT_NAME_MAXLEN];
}Item_PeopleGroupDetailInfo, *pItem_PeopleGroupDetailInfo;

//method: add\del\modify\query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			num;
}Type_PeopleGroupDetailInfo, *pType_PeopleGroupDetailInfo;

//常用人员分组人员
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部
	int32u subid;//如果指定了ID刚表示是对特定人员ID的操作,为0表示分组内的全部人员 
}Type_PeopleInGroup, *pType_PeopleInGroup;

//常用人员分组人员
typedef struct
{
	int32u memberid;
}Item_PeopleInGroupDetailInfo, *pItem_PeopleInGroupDetailInfo;

//call callreturn
//method: save\query\move\del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u			groupid;
	int32u			num;
}Type_PeopleInGroupDetailInfo, *pType_PeopleInGroupDetailInfo;

//添加常用人员分组人员
//call
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u			groupid;
	Item_PersonDetailInfo item;
}Type_AddPeopleToGroup, *pType_AddPeopleToGroup;

//参会人员
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MemberInfo, *pType_MemberInfo;

//参会人员
typedef struct
{
	int32u personid;
	char   name[DEFAULT_NAME_MAXLEN];
	char   company[DEFAULT_DESCRIBE_LENG];
	char   job[DEFAULT_DESCRIBE_LENG];
	char   comment[DEFAULT_DESCRIBE_LENG];
	char   phone[DEFAULT_SHORT_DESCRIBE_LENG];
	char   email[DEFAULT_SHORT_DESCRIBE_LENG];
	char   password[DEFAULT_PASSWORD_LENG];
}Item_MemberDetailInfo, *pItem_MemberDetailInfo;

//method: add\del\modify\query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			personnum;
}Type_MemberDetailInfo, *pType_MemberDetailInfo;

//查询指定参会人员的某项属性
//property id
#define MEETMEMBER_PROPERTY_NAME				1 //名称 query
#define MEETMEMBER_PROPERTY_COMPANY				2 //公司 query
#define MEETMEMBER_PROPERTY_JOB					3 //身份 query
#define MEETMEMBER_PROPERTY_COMMENT				4 //备注 query
#define MEETMEMBER_PROPERTY_PHONE				5 //电话 query
#define MEETMEMBER_PROPERTY_EMAIL				6 //邮箱 query
#define MEETMEMBER_PROPERTY_PASSWORD			7 //密码 query

#define MEET_MEMBERSTRING_MAXLEN 260
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u parameterval;//传入参数 为0表示本机设置定的人员id
	char   propertytext[MEET_MEMBERSTRING_MAXLEN];//字符串

}Type_MeetMemberQueryProperty, *pType_MeetMemberQueryProperty;


//参会人员权限
//type:TYPE_MEET_INTERFACE_MEMBERPERMISSION
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MemberPerMissionInfo, *pType_MemberPerMissionInfo;

//参会人权限
#define memperm_sscreen					0x00000001		//同屏权限
#define memperm_projective				0x00000002		//投影权限
#define memperm_upload					0x00000004		//上传权限
#define memperm_download				0x00000008		//下载权限
#define memperm_vote					0x00000010		//投票权限

typedef struct
{
	unsigned int memberid;
	unsigned int permission;	//参会人权限
}Item_MemberPermission, *pItem_MemberPermission;

//type:TYPE_MEET_INTERFACE_MEMBERPERMISSION
//method: save\query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			num;
}Type_MemberPermission, *pType_MemberPermission;

//查询指定人员的权限
//property id
#define MEETMEMBERPERMISSION_PROPERTY_ALLPERMISSIONS	1 //所有权限 query
#define MEETMEMBERPERMISSION_PROPERTY_SCREEN			2 //同屏权限 query
#define MEETMEMBERPERMISSION_PROPERTY_PROJECTIVE		3 //投影权限 query
#define MEETMEMBERPERMISSION_PROPERTY_UPLOAD			4 //上传权限 query
#define MEETMEMBERPERMISSION_PROPERTY_DOWNLOAD			5 //下载权限 query
#define MEETMEMBERPERMISSION_PROPERTY_VOTE				6 //投票权限 query

//type:TYPE_MEET_INTERFACE_MEMBERPERMISSION
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u memberid;//传入参数 为0表示本机设备ID

	int32u propertyval; //数据
}Type_MemberPermissionQueryProperty, *pType_MemberPermissionQueryProperty;

//参会人员分组
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MemberGroup, *pType_MemberGroup;

//参会人员分组
typedef struct
{
	int32u groupid;
	char   name[DEFAULT_NAME_MAXLEN];
}Item_MemberGroupDetailInfo, *pItem_MemberGroupDetailInfo;

//method: add\del\modify\query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u			num;
}Type_MemberGroupDetailInfo, *pType_MemberGroupDetailInfo;

//参会人员分组人员
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部
	int32u subid;
}Type_MemberGroupItem, *pType_MemberGroupItem;

//参会人员分组人员
typedef struct
{
	int32u memberid;
}Item_MemberInGroupDetailInfo, *pItem_MemberInGroupDetailInfo;

//method: save\query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u			groupid;
	int32u			num;
}Type_MemberInGroupDetailInfo, *pType_MemberInGroupDetailInfo;

//设备会议信息
//method: notify refresh
typedef struct
{
	Type_HeaderInfo hdr;

}Type_DeviceFaceShow, *pType_DeviceFaceShow;

//----------会议签到类型----------
#define signin_direct	0x00		//类型--直接签到
#define signin_psw		0x01		//类型--个人密码签到
#define signin_photo	0x02		//类型--拍照(手写)签到
#define signin_onepsw	0x03		//类型--会议密码签到
#define signin_onepsw_photo	0x04	//类型--会议密码+拍照(手写)签到
#define signin_psw_photo	0x05	//类型--个人密码+拍照(手写)签到
//method: query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u deviceid;		//设备ID
	int32u meetingid;		//会议ID
	int32u memberid;		//人员ID
	int32u roomid;			//会场ID
	int8u  signin_type;
	char meetingname[DEFAULT_DESCRIBE_LENG];	//会议名称
	char membername[DEFAULT_NAME_MAXLEN];     //人员名称
	char company[DEFAULT_DESCRIBE_LENG];		//公司名称
	char job[DEFAULT_DESCRIBE_LENG];			//职位名称
}Type_DeviceFaceShowDetail, *pType_DeviceFaceShowDetail;

//会场信息
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetRoom, *pType_MeetRoom;

//会场信息
typedef struct
{
	int32u roomid;		
	int32u roombgpicid;	
	int32u managerid;		
	char name[DEFAULT_DESCRIBE_LENG];	
	char addr[DEFAULT_DESCRIBE_LENG];    
	char comment[DEFAULT_DESCRIBE_LENG];
}Item_MeetRoomDetailInfo, *pItem_MeetRoomDetailInfo;

//method: save\query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u			num;
}Type_MeetRoomDetailInfo, *pType_MeetRoomDetailInfo;

//查询会场属性信息
//property id
#define MEETROOM_PROPERTY_BGPHOTOID			1 //按会场ID返回会场底图ID query
#define MEETSEAT_PROPERTY_MANAGERID			2 //按会场ID返回会场管理员ID query

//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u parameterval;//传入参数 为0表示当前会场对应的信息
	int32u propertyval;//值 

}Type_MeetRoomQueryProperty, *pType_MeetRoomQueryProperty;

//会场设备信息
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部
	int32u subid;
}Type_MeetRoomDevice, *pType_MeetRoomDevice;

//会场设备信息
//method: add \ del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   roomid;//会场ID
	int32u	 num; //设备数
}Type_MeetRoomModDeviceInfo, *pType_MeetRoomModDeviceInfo;

//direction 会场设备朝向
#define SEATDIRECTION_UP	0 //朝上
#define SEATDIRECTION_DOWN	1 //朝上
#define SEATDIRECTION_LEFT	2 //朝左
#define SEATDIRECTION_RIGHT 3 //朝右

//会场设备信息
typedef struct
{
	int32u devid;
	float  x;
	float  y;
	int    direction;//会场设备朝向
}Item_MeetRoomDevPosInfo, *pItem_MeetRoomDevPosInfo;

//TYPE_MEET_INTERFACE_ROOMDEVICE
//method: query/set
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   roomid;//会场ID
	int32u	 num; //设备数
}Type_MeetRoomDevPosInfo, *pType_MeetRoomDevPosInfo;

//会场设备排位详细信息
typedef struct
{
	int32u devid;
	char   devname[DEVICE_NAME_MAXLEN];
	float  x;
	float  y;
	int    direction;//会场设备朝向

	int32u memberid;
	char   membername[DEFAULT_NAME_MAXLEN];

	int  issignin;//是否已经签到 
	int  role;//参会人员角色 

	int32u	facestate;//界面状态 参见MemState_MainFace 定义
}Item_MeetRoomDevSeatDetailInfo, *pItem_MeetRoomDevSeatDetailInfo;

//type:TYPE_MEET_INTERFACE_ROOMDEVICE
//method: METHOD_MEET_INTERFACE_DETAILINFO, MAKEVIDEO
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   roomid;//会场ID
	int32u	 num; //设备数
}Type_MeetRoomDevSeatDetailInfo, *pType_MeetRoomDevSeatDetailInfo;

//会场设备信息
//filterflag
#define MEET_ROOMDEVICE_FLAG_ONLINE		0x0000001 //在线
#define MEET_ROOMDEVICE_FLAG_FACESTATUS 0x0000002 //匹配会议界面状态
#define MEET_ROOMDEVICE_FLAG_BINDMEMBER 0x0000004 //参会人员绑定
#define MEET_ROOMDEVICE_FLAG_PROJECTIVE 0x0000008 //投影机
#define MEET_ROOMDEVICE_FLAG_HADSIGNIN  0x0000010 //已经签到
#define MEET_ROOMDEVICE_FLAG_NOSELFID   0x0000020 //排除本机ID
#define MEET_ROOMDEVICE_FLAG_NODEVID    0x0000040 //排除指定ID
//查询符合要求的设备ID
//call
//type:TYPE_MEET_INTERFACE_ROOMDEVICE
//method: complexquery
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   filterflag;//查询标志
	int32u   roomid;//会场ID

	int32u   facestatus; //匹配会议界面状态
	int32u   exceptdevid;//排除指定ID
}Type_DumpMeetRoomDevInfo, *pType_DumpMeetRoomDevInfo;

typedef struct
{
	int32u devid;
}Item_ResMeetRoomDevInfo, *pItem_ResMeetRoomDevInfo;

//返回符合要求的设备ID
//callreturn
//type:TYPE_MEET_INTERFACE_ROOMDEVICE
//method: complexquery
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   queryflag;//查询标志

	int32u   facestatus;
	int32u   exceptdevid;

	int32u   roomid;//会场ID
	int32u	 num;   //设备数
}Type_ResMeetRoomDevInfo, *pType_ResMeetRoomDevInfo;

//会场底图信息 
//type:TYPE_MEET_INTERFACE_ROOM
//method: set
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   roomid;//会场ID
	int32u	 bgpicid; //底图媒体ID
}Type_MeetRoomModBGInfo, *pType_MeetRoomModBGInfo;

//会议信息
//type:TYPE_MEET_INTERFACE_MEETINFO
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetInfo, *pType_MeetInfo;

//status 会议状态
#define 	MEETING_STATUS_Ready  0//会议创建中，等待开始
#define 	MEETING_STATUS_Start  1//会议开始进行中
#define 	MEETING_STATUS_End    2//会议结束
#define 	MEETING_STATUS_PAUSE  3//会议暂停

#define SIGNIN_PSW_LEN 16
//会议信息
typedef struct
{
	int32u			id; //会议ID
	char			name[DEFAULT_DESCRIBE_LENG]; //名称
	int32u			roomId; //会场ID，即会议室 
	int				secrecy; //是否为保密会议 1为密保会议
	int64u			startTime; //开始时间 单位:秒
	int64u			endTime;   //结束时间 单位:秒
	int8u			signin_type;//签到类型
	int32u			managerid;//管理员id
	char			onepsw_signin[SIGNIN_PSW_LEN];//会议签到密码
	int32u			status;//会议状态，0为未开始会议，1为已开始会议，2为已结束会议
	char		    ordername[DEFAULT_NAME_MAXLEN];//会议预约人名称
}Item_MeetMeetInfo, *pItem_MeetMeetInfo;

//type:TYPE_MEET_INTERFACE_MEETINFO
//method: add/mod/del/query/dump
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetMeetInfo, *pType_MeetMeetInfo;

//查询会议属性信息
//property id
#define MEET_PROPERTY_SECRECY			1 //按会议ID返回会议是否为保密会议 query
#define MEET_PROPERTY_ROOMID			2 //按会议ID返回会议会场ID query
#define MEET_PROPERTY_SIGNINTYPE		3 //按会议ID返回会议签到方式 query
#define MEET_PROPERTY_MANAGERID			4 //按会议ID返回会议管理员iD query
#define MEET_PROPERTY_STATUS			5 //按会议ID返回会议状态  query
#define MEET_PROPERTY_STARTTIME			6 //按会议ID返回会议开始时间 query
#define MEET_PROPERTY_ENDTIME			7 //按会议ID返回会议结束时间 query

//type:TYPE_MEET_INTERFACE_MEETINFO
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u parameterval;//传入参数 为0表示当前会议
	int32u propertyval;//值 

}Item_MeetMeetInfoQueryProperty_int32u, *pItem_MeetMeetInfoQueryProperty_int32u;

//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u parameterval;//传入参数 为0表示当前会议
	int64u propertyval;//值 

}Item_MeetMeetInfoQueryProperty_int64u, *pItem_MeetMeetInfoQueryProperty_int64u;

//修改会议状态
//type:TYPE_MEET_INTERFACE_MEETINFO
//method: modifystatus
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  meetid;			//会议ID
	int32u  status; //会议状态

}Type_MeetModStatus, *pType_MeetModStatus;

//会议目录
//type:TYPE_MEET_INTERFACE_MEETDIRECTORY
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetDirectory, *pType_MeetDirectory;

//会议目录
typedef struct
{
	int32u			id; //目录ID
	char			name[DEFAULT_DESCRIBE_LENG]; //名称
	int32u			parentid; //父目录ID
	int32u			dirpos; //目录序号
	int			    filenum;//该目录下的文件数量
}Item_MeetDirDetailInfo, *pItem_MeetDirDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETDIRECTORY
//method: add/mod/del/query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetDirDetailInfo, *pType_MeetDirDetailInfo;

typedef struct
{
	int32u dirid;  //会议目录
	int32u pos;    //序号
}Item_MeetingDirPosItem, *pItem_MeetingDirPosItem;

//修改会议目录排序
//type:TYPE_MEET_INTERFACE_MEETDIRECTORY
//method: set
typedef struct
{
	Type_HeaderInfo hdr;
	int32u num;//目录数
	//Item_MeetingDirPosItem  item[];//排序 

}Type_ModMeetDirPos, *pType_ModMeetDirPos;

//查询指定会议目录的某项属性
//property id
#define MEETDIRECTORY_PROPERTY_NAME   1 //目录名称   query(text)
#define MEETDIRECTORY_PROPERTY_SIZE	  2 //文件个数   query(fixed32) 
#define MEETDIRECTORY_PROPERTY_PARENT 3 //父目录ID   query(fixed32) 
#define MEETDIRECTORY_PROPERTY_POS    4 //目录的序号 query(fixed32)

//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID 
	int32u dirid;//传入参数
	int32u propertyval;//返回值

}Type_MeetDirectoryQueryPropertyInt32u, *pType_MeetDirectoryQueryPropertyInt32u;

#define MEET_DIRECTORYSTRING_MAXLEN 260
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u dirid;//传入参数 为0表示当前会议
	char   propertytext[MEET_DIRECTORYSTRING_MAXLEN]; //名称

}Type_MeetDirectoryQueryPropertyString, *pType_MeetDirectoryQueryPropertyString;

//会议目录文件
//type:TYPE_MEET_INTERFACE_MEETDIRECTORYFILE
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部
	int32u subid;
}Type_MeetDirectoryFile, *pType_MeetDirectoryFile;

///start FileScore
typedef struct
{
	int32u fileid;  //文件ID
	int32u memberid;//参会人员ID
	int32u score;//评分
	int64u scoretime;//评分utc时间 微秒
}Item_MeetFileScore, *pItem_MeetFileScore;

//会议文件评分  修改
//会议文件评分相关
//type:TYPE_MEET_INTERFACE_FILESCORE
//method:添加、修改、删除、查询(数据库返回)
typedef struct
{
	Type_HeaderInfo hdr;
	int		 isfirst;//是否为第一帧,如果评分太多,接下来的帧会在下次接收到后回调
	int32u	 num; //pItem_MeetFileScore
	pItem_MeetFileScore pitem;
}Type_MeetFileScore, *pType_MeetFileScore;

//会议文件评分  查询
//会议文件评分相关
//type:TYPE_MEET_INTERFACE_FILESCORE
//method:METHOD_MEET_INTERFACE_ASK\METHOD_MEET_INTERFACE_QUERY
typedef struct
{
	Type_HeaderInfo hdr;

	int32u fileid;   //文件ID
}Type_QueryFileScore, *pType_QueryFileScore;

//会议文件评分  返回查询结果
//会议文件评分相关
//type:TYPE_MEET_INTERFACE_FILESCORE
//method:METHOD_MEET_INTERFACE_ASK
typedef struct
{
	Type_HeaderInfo hdr;

	int32u fileid;   //文件ID
	int32u score;	 //平均评分
}Type_QueryAverageFileScore, *pType_QueryAverageFileScore;
///end FileScore

#define MAX_EVALUATETEXTLEN 260 //评价的文本最大长度
//evaluate flag
#define FILEEVALUATE_FLAG_SECRETARY 0x00000001 //该标志为1表示评价对外不可见

///start Fileevaluate
typedef struct
{
	int32u fileid;  //文件ID
	int32u memberid;//参会人员ID
	int32u flag;//标志 参见evaluate flag 宏定义
	int64u evaluatetime;//评分utc时间 微秒
	char   evaluate[MAX_EVALUATETEXTLEN];//评分的文本
}Item_FileEvaluate, *pItem_FileEvaluate;

//会议文件评价 
//type：TYPE_MEET_INTERFACE_FILEEVALUATE
//method:add、query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   startrow;//查询返回用户传过来的开始行
	int		 isfirst;//是否为第一帧,如果评价太多,接下来的帧会在下次接收到后回调
	int32u	 num; //pItem_FileEvaluate
	pItem_FileEvaluate pitem;
}Type_MeetingFileEvaluate, *pType_MeetingFileEvaluate;

//会议文件评价  查询
//type：TYPE_MEET_INTERFACE_FILEEVALUATE
//method:query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u fileid;   //文件ID 可以为0表示所有文件
	int32u memberid;//参会人员ID 可以为0表示所有参会人
	int64u startevaluatetime;//查询的起始评分utc时间 微秒 必须有效
	int64u endevaluatetime;//查询的结束评分utc时间 微秒 必须有效
	int32u startrow;//查询开始行 实现分页查询 必须有效 第一次从0开始
}Type_QueryFileEvaluate, *pType_QueryFileEvaluate;

//会议文件评价  删除
//type：TYPE_MEET_INTERFACE_FILEEVALUATE
//method:del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u fileid;   //文件ID 可以为0表示所有文件
	int32u memberid;//参会人员ID 可以为0表示所有参会人
	int64u evaluatetime;//评分utc时间 微秒 当fileid、memberid有效时 可以有效表示删除指定的评论
}Type_DelFileEvaluate, *pType_DelFileEvaluate;
///end Fileevaluate

//////////////////////////////////////////////////////////////////////////
///start Meetevaluate
typedef struct
{
	int32u memberid;//参会人员ID
	int32u flag;//标志 参见file evaluate flag 宏定义
	int64u evaluatetime;//评分utc时间 微秒
	char   evaluate[MAX_EVALUATETEXTLEN];//评分的文本
}Item_MeetEvaluate, *pItem_MeetEvaluate;

//会议文件评价 
//type：TYPE_MEET_INTERFACE_MEETEVALUATE
//method:添加、查询(数据库返回)
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   startrow;//查询返回用户传过来的开始行
	int		 isfirst;//是否为第一帧,如果评分太多,接下来的帧会在下次接收到后回调
	int32u	 num; //pItem_MeetEvaluate
	pItem_MeetEvaluate pitem; 
}Type_MeetingMeetEvaluate, *pType_MeetingMeetEvaluate;

//会议文件评价  查询
//stages：TYPE_MEET_INTERFACE_MEETEVALUATE
//method:查询
typedef struct
{
	Type_HeaderInfo hdr;

	int32u memberid;//参会人员ID 可以为0表示所有参会人
	int64u startevaluatetime;//查询的起始评价utc时间 微秒 必须有效
	int64u endevaluatetime;//查询的结束评分utc时间 微秒 必须有效
	int32u startrow;//查询开始行 实现分页查询 必须有效 第一次从0开始
}Type_QueryMeetEvaluate, *pType_QueryMeetEvaluate;

//会议文件评价  删除
//stages：TYPE_MEET_INTERFACE_MEETEVALUATE
//method:删除
typedef struct
{
	Type_HeaderInfo hdr;

	int32u memberid;//参会人员ID 可以为0表示所有参会人
	int64u evaluatetime;//评分utc时间 微秒 当memberid有效时 可以有效表示删除指定的评论
}Type_DelMeetEvaluate, *pType_DelMeetEvaluate;
///end Meetevaluate

//////////////////////////////////////////////////////////////////////////
///start systemlog
#define MAX_SYSTEMLOG_TEXTLEN 260 //记录文本最大长度
typedef struct
{
	int32u stage;//操作类别 参见STARG_ENUM
	int32u method;//操作方法 参见 METHOD_ENUM
	int32u adminid;//管理人员ID
	int32u deviceid;//操作的设备ID
	int32u meetingid;//操作的会议ID
	int64u logtime;//操作utc时间 微秒
	char   logmsg[MAX_SYSTEMLOG_TEXTLEN];//记录文本
}Item_MeetSystemLog, *pItem_MeetSystemLog;

//管理日志 
//stages：TYPE_MEET_INTERFACE_SYSTEMLOG
//method:查询(数据库返回)
typedef struct
{
	Type_HeaderInfo hdr;

	int32u   startrow;//查询返回用户传过来的开始行
	int		 isfirst;//是否为第一帧,如果评分太多,接下来的帧会在下次接收到后回调
	int32u	 num; //pItem_MeetSystemLog
	pItem_MeetSystemLog pitem;
}Type_MeetingMeetSystemLog, *pType_MeetingMeetSystemLog;

//管理日志 
//stages：TYPE_MEET_INTERFACE_SYSTEMLOG
//method:查询
typedef struct
{
	Type_HeaderInfo hdr;

	int32u stage;//操作类别 参见STARG_ENUM 可以为0表示所有类别
	int32u method;//操作方法  参见 METHOD_ENUM 可以为0表示所有方法

	int32u meetingid;//会议ID 可以为0表示所有会议
	int32u adminid;//管理人员ID 可以为0表示所有管理人员
	int32u deviceid;//操作的设备ID 可以为0表示所有设备

	int64u startlogtime;//查询的起始记录utc时间 微秒 必须有效
	int64u endlogtime;//查询的结束记录utc时间 微秒 必须有效
	int32u startrow;//查询开始行 实现分页查询 必须有效
}Type_QueryMeetSystemLog, *pType_QueryMeetSystemLog;
///end systemlog

//----------参会人员角色----------
#define role_member_nouser 		0x00  //未使用
#define role_member_normal 		0x01  //一般参会人员
#define role_member_compere 	0x03  //主持人
#define role_member_secretary 	0x04  //秘书
#define role_device_projector	0x08  //投影仪
#define role_admin				0x09  //管理员

//文件attrib
#define MEETFILE_ATTRIB_IMPORTANTFILE 0x000000001 //紧急文件

//会议目录文件
typedef struct
{
	int32u			mediaid; //文件ID
	char			name[DEFAULT_FILENAME_LENG]; //文件名称
	int32u			uploaderid; //上传者ID
	int8u			uploader_role; //上传者角色
	int32u			mstime;//时间 毫秒
	int64u			size;//大小 字节
	int32u			attrib;//文件属性
	int32u			filepos;// 文件序号
	char			uploader_name[DEFAULT_NAME_MAXLEN]; //上传者名称
}Item_MeetDirFileDetailInfo, *pItem_MeetDirFileDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETDIRECTORYFILE
//method: add/mod/del/query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   dirid;
	int32u	 num; //
}Type_MeetDirFileDetailInfo, *pType_MeetDirFileDetailInfo;

//有新的录音文件媒体文件通知
//type:TYPE_MEET_INTERFACE_MEETDIRECTORYFILE
//callback
//method: add
typedef struct
{
	Type_HeaderInfo hdr;
	int32u mediaid;
	int8u  filename[DEFAULT_FILENAME_LENG];//文件名
	int64u len;//大小 字节
	int32u msec; //播放时长，以ms为单位,如果为升级文件 记录升级的版本信息数据长度
}Type_MeetNewRecordFile, *pType_MeetNewRecordFile;

//查询指定文件的某项属性
//property id
#define MEETFILE_PROPERTY_NAME   1 //名称 query(text)
#define MEETFILE_PROPERTY_SIZE	 2 //大小 query(fixed64) 字节
#define MEETFILE_PROPERTY_TIME   3 //时长 query(fixed32) 毫秒
#define MEETFILE_PROPERTY_ATTRIB 4 //文件attrib query(fixed32)
#define MEETFILE_PROPERTY_AVAILABLE 5 //文件是否可用 query 不存在返回ERROR_MEET_INTERFACE_NOFIND

//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID 
	int32u mediaid;//传入参数
	int32u propertyval;//返回值

}Type_MeetMFileQueryPropertyInt32u, *pType_MeetMFileQueryPropertyInt32u;

//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u mediaid;  //传入参数
	int64u propertyval;//返回值

}Type_MeetMFileQueryPropertyInt64u, *pType_MeetMFileQueryPropertyInt64u;

#define MEET_FILESTRING_MAXLEN 260
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u mediaid;//传入参数
	char   propertytext[MEET_FILESTRING_MAXLEN];//字符串

}Type_MeetMFileQueryPropertyString, *pType_MeetMFileQueryPropertyString;

//修改会议目录文件
typedef struct
{
	int32u			mediaid; //文件ID
	char			name[DEFAULT_FILENAME_LENG]; //文件名称
	int32u			attrib;//文件属性
}Item_ModMeetDirFile, *pItem_ModMeetDirFile;

//type:TYPE_MEET_INTERFACE_MEETDIRECTORYFILE
//method: modifyinfo
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   dirid;
	int32u	 num; //
}Type_ModMeetDirFile, *pType_ModMeetDirFile;

//修改会议目录文件排序
//type:TYPE_MEET_INTERFACE_MEETDIRECTORYFILE
//method: set
typedef struct
{
	Type_HeaderInfo hdr;

	int32u dirid;//目录ID
	int32u filenum;//文件数
	//int32u  fileid[];//按ID的排序 

}Type_ModMeetDirFilePos, *pType_ModMeetDirFilePos;

//queryflag
#define MEET_FILETYPE_QUERYFLAG_UPLOADID		 0x00000001  //上传者有效
#define MEET_FILETYPE_QUERYFLAG_FILETYPE		 0x00000002  //文件类型有效
#define MEET_FILETYPE_QUERYFLAG_ATTRIB			 0x00000004  //文件属性有效
#define MEET_FILETYPE_QUERYFLAG_IMPORTANTFRONT	 0x00000008  //attrib为MEETFILE_ATTRIB_IMPORTANTFILE的文件会被放在前面返回

//filetype
#define MEET_FILETYPE_AUDIO		 0x00000001  //音频
#define MEET_FILETYPE_VIDEO		 0x00000002  //视频
#define MEET_FILETYPE_PICTURE	 0x00000004  //图片
#define MEET_FILETYPE_DOCUMENT	 0x00000008  //文档
#define MEET_FILETYPE_OTHER		 0x00000010  //其它

//type:TYPE_MEET_INTERFACE_MEETDIRECTORYFILE
//method: complexpagequery
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   dirid;//为0表示从平台里查询(平台里查询这种情况下 role和uploadid是无效的)

	int32u   queryflag;//查询标志

	int		 role;//上传者角色
	int32u   uploadid;//上传人员ID 为0表示全部

	int32u	 filetype;//文件类型 为0表示全部

	int32u   attrib;//文件属性 为0表示全部
	int		 pageindex;//分页值
	int		 pagenum;//分页大小 为0表示返回全部
}Type_ComplexQueryMeetDirFile, *pType_ComplexQueryMeetDirFile;

//会议目录权限
//type:TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetDirectoryRight, *pType_MeetDirectoryRight;

//会议目录权限
typedef struct
{
	int32u			memberid; //参会人员ID
}Item_MeetDirRightDetailInfo, *pItem_MeetDirRightDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT
//method: save/query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   dirid;
	int32u	 num; //
}Type_MeetDirRightDetailInfo, *pType_MeetDirRightDetailInfo;

//会议视频
//type:TYPE_MEET_INTERFACE_MEETVIDEO
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetVideo, *pType_MeetVideo;

//会议视频相关
typedef struct
{
	int32u			id; //流通道ID
	int32u		    deviceid;
	int8u			subid;
	char			name[DEFAULT_DESCRIBE_LENG]; //流通道名称
	char			addr[DEFAULT_DESCRIBE_LENG]; //流通道地址
}Item_MeetVideoDetailInfo, *pItem_MeetVideoDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETVIDEO
//method: add/mod/del/query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetVideoDetailInfo, *pType_MeetVideoDetailInfo;

//会议双屏显示信息
//type:TYPE_MEET_INTERFACE_MEETTABLECARD
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
}Type_MeetTableCard, *pType_MeetTableCard;

//type
#define conf_mtgname	0x01 //会议名称
#define conf_memname	0x02 //参会人员名称
#define conf_job		0x03 //参会人员职业
#define conf_company	0x04 //参会人员单位
#define conf_position	0x05 //座位名称

//flag
#define MEET_TABLECARDFLAG_SHOW			0x00000001 //该位用于表示该项是否可见
#define MEET_TABLECARDFLAG_BOLD			0x00000002 //加粗
#define MEET_TABLECARDFLAG_LEAN			0x00000004 //倾斜
#define MEET_TABLECARDFLAG_UNDERLINE	0x00000008 //下划线

//会议双屏显示相关
typedef struct
{
	char fontname[DEFAULT_NAME_MAXLEN];//使用字体名称
	int8u  fontsize;//字体 大小
	int32u fontcolor;//字体颜色
	float lx;//左上角 x坐标 相对宽的百分比 如：lx=0.1 当宽为1920时 真正的lx应该= 0.1 * 1920 = 192
	float ly;
	float rx;
	float ry;
	int32u flag;
	int16u align;//参见本页 font align flag
	int8u  type;//显示的信息
}Item_MeetTableCardDetailInfo, *pItem_MeetTableCardDetailInfo;

#define TABLECARD_MODFLAG_SETDEFAULT 0x00000001 //设置为默认
//type:TYPE_MEET_INTERFACE_MEETTABLECARD
//method: mod/query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   modifyflag;//修改标志
	int32u   bgphotoid;//底图ID
	int32u	 num; //必须是3个
}Type_MeetTableCardDetailInfo, *pType_MeetTableCardDetailInfo;

//会议排位信息
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetSeat, *pType_MeetSeat;

//会议排位信息
typedef struct
{
	int32u			nameId;//参会人员ID
	int32u			seatid;//设备ID
	int				role;  //人员身份 

}Item_MeetSeatDetailInfo, *pItem_MeetSeatDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETSEAT
//method: mod/query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetSeatDetailInfo, *pType_MeetSeatDetailInfo;

//查询会议排位属性信息
//property id
#define MEETSEAT_PROPERTY_SEATID			1 //按人员ID返回设备ID query
#define MEETSEAT_PROPERTY_MEMBERID			2 //按设备ID返回人员ID query
#define MEETSEAT_PROPERTY_ROLEBYMEMBERID				3 //按人员ID返回人员role query
#define MEETSEAT_PROPERTY_ROLEBYDEVICEID				4 //按人员ID返回人员role query
#define MEETSEAT_PROPERTY_ISCOMPEREEBYMEMBERID		5 //按人员ID判断当前人员是不是主持人 query
#define MEETSEAT_PROPERTY_ISCOMPEREEBYDEVICEID		6 //按设备ID判断当前人员是不是主持人 query
#define MEETSEAT_PROPERTY_MEMBERIDBYROLE	7 //返回等于role的人员ID query
#define MEETSEAT_PROPERTY_DEVICEIDBYROLE	8 //返回等于role的设备ID query
#define MEETSEAT_PROPERTY_COMPEEMEMID	9 //返回当前会议主持人的人员ID query
#define MEETSEAT_PROPERTY_COMPEEDEVID	10 //返回当前会议主持人的设备ID query

//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u parameterval;//传入参数 为0表示获取本机的对应信息
	int32u propertyval;//值 
	
}Type_MeetSeatQueryProperty, *pType_MeetSeatQueryProperty;

//会议交流信息
//type
#define MEETIM_CHAT_Message 0 //文本消息
#define MEETIM_CHAT_Link 1	  //多媒体链接
#define MEETIM_CHAT_Water 2		//水
#define MEETIM_CHAT_Tea 3		//茶
#define MEETIM_CHAT_Coffee 4    //咖啡
#define MEETIM_CHAT_Pen 5		//笔
#define MEETIM_CHAT_Paper 6		//纸
#define MEETIM_CHAT_Technical 7//技术员
#define MEETIM_CHAT_Waiter 8//服务员
#define MEETIM_CHAT_Other 9		//其他服务
#define MEETIM_CHAT_Emecc 10	//申请主持

#define MEETIM_CHAR_MSG_MAXLEN 300
//callback
//type:TYPE_MEET_INTERFACE_MEETIM
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int8u	    msgtype;//消息类型
	int8u		role;//发送者角色
	int32u	    memberid;//发送者ID
	char		msg[MEETIM_CHAR_MSG_MAXLEN];//消息文本
	int64u	    utcsecond;//发送UTC时间

	//针对会议茶水服务提供的
	char meetname[DEFAULT_DESCRIBE_LENG];//会议名称
	char roomname[DEFAULT_DESCRIBE_LENG];//会议室名
	char membername[DEFAULT_DESCRIBE_LENG];//人员名称
	char seatename[DEFAULT_DESCRIBE_LENG];//席位名
}Type_MeetIM, *pType_MeetIM;

//call
//type:TYPE_MEET_INTERFACE_MEETIM
//method: send
typedef struct
{
	Type_HeaderInfo hdr;

	int8u	    msgtype;//消息类型
	char		msg[MEETIM_CHAR_MSG_MAXLEN];//消息文本
	int			usernum;//为0表示当前会议全部 如果是会议服务类请求 usernum=0
	//int32u      userids[];
}Type_SendMeetIM, *pType_SendMeetIM;

//会议交流信息
typedef struct
{
	int8u	    msgtype;//消息类型
	int8u		role;//发送者角色
	int32u	    memberid;//发送者ID
	char		msg[MEETIM_CHAR_MSG_MAXLEN];//消息文本
	int64u	    utcsecond;//发送UTC时间

}Item_MeetIMDetailInfo, *pItem_MeetIMDetailInfo;

//callreturn
//type:TYPE_MEET_INTERFACE_MEETIM
//method: query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetIMDetailInfo, *pType_MeetIMDetailInfo;

//queryflag 需要按那个查询那位置为1
#define COMPLEXQUERY_MSGTYPE	0x0000001
#define COMPLEXQUERY_ROLE		0x0000002
#define COMPLEXQUERY_MEMBERID	0x0000004
#define COMPLEXQUERY_UTCTIME	0x0000008

//组合查询
//type:TYPE_MEET_INTERFACE_MEETIM
//method: complexquery （pTypePageResQueryInfo 使用按页查询方式返回结果)
typedef struct
{
	Type_HeaderInfo hdr;

	int32u      queryflag; //查询标志

	int8u	    msgtype;//消息类型
	int8u		role;//发送者角色
	int32u	    memberid;//发送者ID

	//时间范围
	int64u	    startutcsecond;//发送UTC时间
	int64u	    endutcsecond;//发送UTC时间

	int			pageindex;//起始索引 从0 开始
	int			pagenum;//每页项数

}Type_MeetComplexQueryIM, *pType_MeetComplexQueryIM;

//投票状态 votestate 
#define vote_notvote 0 //未发起的投票
#define vote_voteing 1 //正在进行的投票
#define vote_endvote 2 //已经结束的投票

//maintype //投票 选举 调查问卷
#define VOTE_MAINTYPE_vote			0 //投票
#define VOTE_MAINTYPE_election		1 //选举
#define VOTE_MAINTYPE_questionnaire 2 //问卷调查

//mode
#define VOTEMODE_agonymous	0 //匿名投票
#define VOTEMODE_signed		1 //记名投票

//type
#define VOTE_TYPE_MANY			    0 //多选
#define VOTE_TYPE_SINGLE			1 //多选
#define VOTE_TYPE_4_5			    2 //5选4
#define VOTE_TYPE_3_5			    3 //5选2
#define VOTE_TYPE_2_5			    4 //5选2
#define VOTE_TYPE_2_3			    5 //3选2

//会议发起投票信息
//type:TYPE_MEET_INTERFACE_MEETONVOTING
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetOnVoting, *pType_MeetOnVoting;

//会议发起投票信息
typedef struct
{
	int32u		voteid;
	char		content[DEFAULT_VOTECONTENT_LENG]; //投票内容 
	int			maintype; //投票 选举 调查问卷
	int			mode; //匿名投票 记名投票
	int			type; //多选 单选
	int32u		timeouts;  //超时值
	int32u		selectcount; //有效选项
	char		text[MEET_MAX_VOTENUM][DEFAULT_VOTEITEM_LENG];  //选项描述文字

	//发起投票的参数,只在使用查询方法时有效,其它方法要忽略
	int32u      voteflag; //发起投票标志 参见MEET_VOTING_FLAG_NOPOST 定义
	int			membernum;
	//int32u	memberids[];
}Item_MeetOnVotingDetailInfo, *pItem_MeetOnVotingDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETONVOTING
//method: query/add/mod/
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetOnVotingDetailInfo, *pType_MeetOnVotingDetailInfo;

//设置投投票计时值
//type:TYPE_MEET_INTERFACE_MEETONVOTING
//method: set
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 voteid; //投票ID
	int32u   timeouts;//计时
}Type_MeetSetVoteTimeouts, *pType_MeetSetVoteTimeouts;

//会议发起投票信息
//voteflag
#define MEET_VOTING_FLAG_NOPOST	   0x00000001 //不在投影机上显示投票结果
#define MEET_VOTING_FLAG_SECRETARY 0x00000002 //投票选项保密投票模式
#define MEET_VOTING_FLAG_AUTOEXIT  0x00000004 //自动超时结束 --由发起方计时发送结束
#define MEET_VOTING_FLAG_REVOTE    0x00000008 //重投 清空之前的记录

typedef struct
{
	int32u memberid;
}SubItem_VoteMemFlag, *pSubItem_VoteMemFlag;

typedef struct
{
	int32u voteid;//发起的投票ID
	int32u voteflag; //发起投票标志 参见MEET_VOTING_FLAG_NOPOST 定义
	int32u timeouts; //计时结束 单位：秒
	int32u membernum;//参与投票的参会人员数
	//SubItem_VoteMemFlag members[];
}Item_VoteStartFlag, *pItem_VoteStartFlag;

//type:TYPE_MEET_INTERFACE_MEETONVOTING
//method: start
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetStartVoteInfo, *pType_MeetStartVoteInfo;

//会议停止、删除投票信息
typedef struct
{
	int32u		voteid;
}Item_MeetStopVoteInfo, *pItem_MeetStopVoteInfo;

//type:TYPE_MEET_INTERFACE_MEETONVOTING
//method: stop/del
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetStopVoteInfo, *pType_MeetStopVoteInfo;

//投票标志 selitem高8位
#define VOTE_SELFLAG_MASK     0xff000000 //掩码
#define VOTE_SELFLAG_CHECKIN  0x80000000 //该位为1表示已经签到

#define VOTE_CHECKIN_BIT_INDEX  31 //签到掩码位

//提交会议投票信息
typedef struct
{
	int32u		voteid;
	int			selcnt;//有效选项数
	int32u		selitem;//选择的项 0x00000001 选择了第一项 0x00000002第二项 对应项位置1表示选择 | 高8位保留
}Item_MeetSubmitVote, *pItem_MeetSubmitVote;

//type:TYPE_MEET_INTERFACE_MEETONVOTING
//method: submit
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetSubmitVote, *pType_MeetSubmitVote;

//会议投票信息
//type:TYPE_MEET_INTERFACE_MEETVOTEINFO
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetVoteInfo, *pType_MeetVoteInfo;

//会议投票信息
typedef struct
{
	char    text[DEFAULT_VOTEITEM_LENG];//描述文字
	int32u  selcnt;	 //投票数
}SubItem_VoteItemInfo, *pSubItem_VoteItemInfo;

typedef struct
{
	int32u		voteid;
	char		content[DEFAULT_VOTECONTENT_LENG]; //投票内容 
	int			maintype;//类别 投票 选举 调查问卷
	int			mode; //匿名投票 记名投票
	int			type; //多选 单选
	int			votestate; //投票状态
	int32u		timeouts;  //超时值
	int32u						selectcount; //有效选项
	SubItem_VoteItemInfo		item[MEET_MAX_VOTENUM];  //选项描述文字

}Item_MeetVoteDetailInfo, *pItem_MeetVoteDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETVOTEINFO
//method: query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetVoteDetailInfo, *pType_MeetVoteDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETVOTEINFO
//method: complexquery
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 maintype; //指定大类 参见本文件中的maintype 定义
}Type_MeetVoteComplexQuery, *pType_MeetVoteComplexQuery;

//会议投票人
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetVoteSignedInfo, *pType_MeetVoteSignedInfo;

//会议投票人
typedef struct
{
	int32u		 id;//投票人员
	int32u       selcnt; //投票选项 0x00000001 选择了第一项 0x00000002第二项 对应项位置1表示选择
}Item_MeetVoteSignInDetailInfo, *pItem_MeetVoteSignInDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETVOTESIGNED
//method: query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   voteid;
	int32u	 num; //
}Type_MeetVoteSignInDetailInfo, *pType_MeetVoteSignInDetailInfo;

//查询会议投票人属性信息
//property id
#define MEETVOTE_PROPERTY_HADSIGNIN			    1 //按投票ID\人员ID返回是否已经投票 query
#define MEETVOTE_PROPERTY_SIGNINITEM		    2 //按投票ID\人员ID返回投票项记录 //0x0001选择了选项一,0x0010选择了选项二 query
#define MEETVOTE_PROPERTY_HADCHECKINNUM		    3 //按投票ID\人员ID返回是否已经投票签到 query
#define MEETVOTE_PROPERTY_CHECKINNUM		    4 //按投票ID\返回投票实际签到总人数  query
#define MEETVOTE_PROPERTY_ATTENDNUM		        5 //按投票ID\返回投票应到总人数  query
#define MEETVOTE_PROPERTY_VOTEDNUM		        6 //按投票ID\返回投票已经投票总人数  query

//type:TYPE_MEET_INTERFACE_MEETVOTESIGNED
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u voteid;//传入参数
	int32u memberid;//传入参数 为0表示本身
	int32u propertyval;//返回值 

}Type_MeetVoteQueryProperty, *pType_MeetVoteQueryProperty;

//会议签到
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetSignin, *pType_MeetSignin;

//会议签到
typedef struct
{
	int32u		nameId; //人员ID
	int64u		utcseconds;  //日期 时间 单位:秒
	int8u		signin_type;//签到方式

	int			signdatalen;
	//char		psigndata[signdatalen]; //签到数据 如果是密码签到则为签到密码,图片签到则为png图片数据

}Item_MeetSignInDetailInfo, *pItem_MeetSignInDetailInfo;

//type:TYPE_MEET_INTERFACE_MEETVOTESIGNED
//method: query/add
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetSignInDetailInfo, *pType_MeetSignInDetailInfo;

//查询会议签到属性信息
//property id
#define MEETSIGN_PROPERTY_HADSIGNIN			    1 //按人员ID返回是否已经签到 query
#define MEETSIGN_PROPERTY_SIGNINTYPE			2 //按人员ID返回签到类型 query

//type:TYPE_MEET_INTERFACE_MEETSIGN
//method: queryproperty
typedef struct
{
	Type_HeaderInfo hdr;

	int32u propertyid;//数据ID
	int32u parameterval;//传入参数 为0表示获取本机的对应人员签到信息
	int32u propertyval;//值 

}Type_MeetSignInQueryProperty, *pType_MeetSignInQueryProperty;

//发送会议签到
//type:TYPE_MEET_INTERFACE_MEETVOTESIGNED
//method: add
typedef struct
{
	Type_HeaderInfo hdr;
	
	int8u		signin_type;//签到方式

	char		password[DEFAULT_PASSWORD_LENG];//签到密码
	int			signdatalen;//图片签到数据大小
	//char		psigndata[signdatalen]; //签到数据 如果是密码签到则为签到密码,图片签到则为png图片数据
}Type_DoMeetSignIno, *pType_DoMeetSignIno;

//会议白板操作

//operflag
#define MEETPOTIL_FLAG_FORCEOPEN	   1 //该标志表示需要强制执行批注白板强行打开
#define MEETPOTIL_FLAG_FORCECLOSE	   2 //该标志表示需要强制执行批注白板强行关闭
#define MEETPOTIL_FLAG_REQUESTOPEN	   3 //该标志表示有人发起白板
#define MEETPOTIL_FLAG_REJECTOPEN	   4 //该标志表示对方拒绝打开白板
#define MEETPOTIL_FLAG_EXIT			   5 //该标志表示有人离开白板
#define MEETPOTIL_FLAG_ENTER		   6 //该标志表示有人进入白板

//执行一个白板操作 用于会议交流 指定需要执行操作的标志
//call
//method: control 
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  operflag;//指定操作标志
	char    medianame[DEFAULT_DESCRIBE_LENG];	//白板操作描述
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

	int		usernum;//参会人员数量
	//int32u userid[usernum];
}Type_MeetWhiteBoardControl, *pType_MeetWhiteBoardControl;

//callback
//method: ask 回调函数返回1表示同意加入，其它值表示拒绝加入
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  operflag;//根据该值判断
	char    medianame[DEFAULT_DESCRIBE_LENG];	//白板描述
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

}Type_MeetStartWhiteBoard, *pType_MeetStartWhiteBoard;

//被邀请人拒绝加入你发起的白板回调
//callback/call
//method: reject
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

}Type_MeetRejectWhiteBoard, *pType_MeetRejectWhiteBoard;

//被邀请人退出了你发起的白板回调
//callback
//method: exit
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

}Type_MeetExitWhiteBoard, *pType_MeetExitWhiteBoard;

//被邀请人加入了你发起的白板回调
//callback/call
//method: enter
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

}Type_MeetEnterWhiteBoard, *pType_MeetEnterWhiteBoard;

//白板操作类型
#define  WB_FIGURETYPE_INK			1 //线条
#define  WB_FIGURETYPE_LINE		2 //直线
#define  WB_FIGURETYPE_ELLIPSE		3 //椭圆
#define  WB_FIGURETYPE_RECTANGLE	4 //矩形
#define  WB_FIGURETYPE_FREETEXT	5 //文字
#define  WB_FIGURETYPE_PICTURE		6 //
#define  WB_FIGURETYPE_ARROW		7 //箭头

//callback
//method: clear, del
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  operid;	    //操作ID 终端计算产生 为0表示清除所有等于opermemberid的白板操作
	int32u  opermemberid;//当前该命令的人员ID 为0表示清除所有等于opermemberid的白板操作
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

	int64u  utcstamp;	//时间戳
	int     figuretype;	//图形类型 为0表示清除所有等于figuretype的白板操作
}Type_MeetClearWhiteBoard, *pType_MeetClearWhiteBoard;

//(call)
//method: delall, del 
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  memberid;   //需要删除的人员ID 为0表示清除所有等于人员的白板操作
	int32u  operid;	    //操作ID 终端计算产生 为0表示清除所有等于opermemberid的白板操作

	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用

	int64u  utcstamp;	//时间戳
	int     figuretype;	//图形类型 为0表示清除所有类型的白板操作
}Type_MeetDoClearWhiteBoard, *pType_MeetDoClearWhiteBoard;

//白板项普通查询
//(call)
//method: query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int     figuretype;	//图形类型 必须指定一种类型,因为不同类型结构不同,不好解析,所以要按类型查询

}Type_MeetWhiteBoardQuery, *pType_MeetWhiteBoardQuery;

//(call)
//method: complexquery
//queryflag 指定有效的查询标志
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int     figuretype;	//图形类型 //图形类型 必须指定一种类型,因为不同类型结构不同,不好解析,所以要按类型查询

	int32u  opermemberid;//人员ID

}Type_MeetWhiteBoardComplexQuery, *pType_MeetWhiteBoardComplexQuery;

//ink
//callback\call
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u utcstamp;	//时间戳
	int    figuretype;	//图形类型

	int8u  linesize;			//线条宽度
	int32u Argb;			//线条颜色

	int32u ptnum;//ink point Num, float[2 * ptnum](x,y依次排列)
	float* pinklist;
	
}Type_MeetWhiteBoardInkItem, *pType_MeetWhiteBoardInkItem;

//查询Ink白板操作
//callreturn
//method: query pagequery
typedef struct
{
	int32u  operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int64u  utcstamp;	//时间戳

	int8u  linesize;			//线条宽度
	int32u Argb;			//线条颜色

	int32u ptnum;//ink point Num, float[2 * ptnum](x,y依次排列)
	//float pinklist[2 * ptnum];

}Item_MeetWBInkDetail, *pItem_MeetWBInkDetail;

typedef struct
{
	Type_HeaderInfo hdr;

	int32u num;	    //
}Type_MeetWBInkDetail, *pType_MeetWBInkDetail;

//rect line elipse
//callback\call
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u utcstamp;	//时间戳
	int    figuretype;	//图形类型

	int8u  linesize;			//线条宽度
	int32u Argb;			//线条颜色
	float  pt[4];		    //(lx,ly,rx,ry 左上角,右下角坐标)

}Type_MeetWhiteBoardRectItem, *pType_MeetWhiteBoardRectItem;

//callreturn
//method: query pagequery
typedef struct
{
	int32u  operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u  utcstamp;	//时间戳
	int     figuretype;	//图形类型

	int8u   linesize;			//线条宽度
	int32u  Argb;			//线条颜色
	float   pt[4];		    //(lx,ly,rx,ry 左上角,右下角坐标)

}Item_MeetWBRectDetail, *pItem_MeetWBRectDetail;

typedef struct
{
	Type_HeaderInfo hdr;

	int32u num;	    //
}Type_MeetWBRectDetail, *pType_MeetWBRectDetail;


//font flag
#define WHITEBOARD_FONT_STRIKEOUT 0x01 //删除线
#define WHITEBOARD_FONT_BOLD	  0x02 //加粗
#define WHITEBOARD_FONT_ITALIC	  0x04 //倾斜
#define WHITEBOARD_FONT_UNDERLINE 0x08 //下划线

#define FONTNAME_LENG	48  //名称最大长度，包含字符串结束符，包括人员名字，设备名称
//callback\call
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u utcstamp;	//时间戳
	int    figuretype;	//图形类型

	int8u  fontsize;			//字体大小
	int8u  fontflag;			//字体其它属性
	int32u Argb;				//字体颜色
	char   fontname[FONTNAME_LENG]; //字体名称
	float  pos[2];				//(lx,ly,左上角坐标)

	int32u textlen;			    //char[](长度为 textlen)
	char*  ptext;
}Type_MeetWhiteBoardTextItem, *pType_MeetWhiteBoardTextItem;

//callreturn
//method: query pagequery
typedef struct
{
	int32u operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u  utcstamp;	//时间戳
	int     figuretype;	//图形类型

	int8u  fontsize;			//字体大小
	int8u  fontflag;			//字体其它属性
	int32u Argb;				//字体颜色
	char   fontname[FONTNAME_LENG]; //字体名称
	float  pos[2];				//(lx,ly,左上角坐标)

	int32u textlen;			    //char[](长度为 textlen)
	//char  ptext[textlen];

}Item_MeetWBTextDetail, *pItem_MeetWBTextDetail;

typedef struct
{
	Type_HeaderInfo hdr;

	int32u num;	    //
}Type_MeetWBTextDetail, *pType_MeetWBTextDetail;

//callback call
//method: add
typedef struct
{
	Type_HeaderInfo hdr;

	int32u operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u utcstamp;	//时间戳
	int    figuretype;	//图形类型

	float  pos[2];				//(lx,ly,左上角坐标)

	int32u picsize;			    //char[](长度为 picsize)
	char*  picdata;
}Type_MeetWhiteBoardPicItem, *pType_MeetWhiteBoardPicItem;

//call return
//method: query pagequery
typedef struct
{
	int32u operid;	    //操作ID 终端计算产生
	int32u  opermemberid;//当前该命令的人员ID
	int32u  srcmemid;//发起人的人员ID 白板标识使用
	int64u  srcwbid;//发起人的白板标识 取微秒级的时间作标识 白板标识使用
	int64u utcstamp;	//时间戳
	int    figuretype;	//图形类型

	float  pos[2];				//(lx,ly,左上角坐标)

	int32u picsize;			    //char[](长度为 picsize)
	//char  picdata[picsize];

}Item_MeetWBPictureDetail, *pItem_MeetWBPictureDetail;

typedef struct
{
	Type_HeaderInfo hdr;

	int32u num;	    //
}Type_MeetWBPictureDetail, *pType_MeetWBPictureDetail;

//会议功能
//type:TYPE_MEET_INTERFACE_FUNCONFIG
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetFunConfig, *pType_MeetFunConfig;

//功能标识码
#define MEET_FUNCODE_AGENDA_BULLETIN	0	//会议议程
#define MEET_FUNCODE_MATERIAL		1	//会议资料
#define MEET_FUNCODE_SHAREDFILE		2	//	共享文件
#define MEET_FUNCODE_POSTIL			3	//批注文件
#define MEET_FUNCODE_MESSAGE			4	//会议交流
#define MEET_FUNCODE_VIDEOSTREAM		5	//视频直播
#define MEET_FUNCODE_WHITEBOARD		6	//白板
#define MEET_FUNCODE_WEBBROWSER		7	//网页
#define MEET_FUNCODE_VOTERESULT		8	//投票
#define MEET_FUNCODE_SIGNINRESULT	9	//签到
#define MEET_FUNCODE_DOCUMENT		10	//外部文档

// 会议系统标准版 增加的功能识别码
#define MEET_FUNCODE_DATAREVIEW     11  // 资料评审
#define MEET_FUNCODE_MEETSERVICE    12  // 会议服务
#define MEET_FUNCODE_MEETNOTE       13  // 会议笔记
#define MEET_FUNCODE_WINDESK        14  // 桌面浏览
#define MEET_FUNCODE_OTHERFUNC      15  // 其他功能

#define MEET_FUNCODE_QUESTIONNAIRE  30  // 问卷调查
#define MEET_FUNCODE_SCORE          31  // 文件评分

//会议功能
typedef struct
{
	int32u	funcode;	//功能标识码
	int32u	position;	//位置标识码

}Item_MeetFunConfigDetailInfo, *pItem_MeetFunConfigDetailInfo;

#define FUNCONFIG_MODFLAG_SETDEFAULT 0x00000001 //设置为默认
//type:TYPE_MEET_INTERFACE_FUNCONFIG
//method: query/save/
typedef struct
{
	Type_HeaderInfo hdr;
	int32u   modifyflag;//修改标志
	int32u	 num; //
}Type_MeetFunConfigDetailInfo, *pType_MeetFunConfigDetailInfo;

//参会人白板颜色
//callback
//type:TYPE_MEET_INTERFACE_MEMBERCOLOR
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部

}Type_MeetMemberColor, *pType_MeetMemberColor;

//参会人白板颜色
typedef struct
{
	int32u memberid;//参会人员ID
	int32u rgb;//颜色值

}Item_MeetMemberColorDetailInfo, *pItem_MeetMemberColorDetailInfo;

//call
//type:TYPE_MEET_INTERFACE_MEMBERCOLOR
//method: query/mod/
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetMemberColorDetailInfo, *pType_MeetMemberColorDetailInfo;

//屏幕鼠标控制
//callback
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  flag;			//控制的标志  参考streamcontrol.h 定义
	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID
	float   x; //x% width的百分
	float   y; //y% height的百分
}Type_MeetScreenMouseControl, *pType_MeetScreenMouseControl;

//屏幕鼠标控制
//call
//method: control
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  flag;			//控制的标志  参考streamcontrol.h 定义
	int32u  deviceid;       //设备ID
	float   x; //x / width的百分比
	float   y; //y / height的百分比
}Type_MeetDoScreenMouseControl, *pType_MeetDoScreenMouseControl;

//屏幕键盘控制
//callback
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  flag;			//控制的标志 参考streamcontrol.h 定义
	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID

	int32u  otherflag; //参考streamcontrol.h 定义
	int32u  key; //对应的key值 参考streamcontrol.h 定义
}Type_MeetScreenKeyBoardControl, *pType_MeetScreenKeyBoardControl;

//屏幕键盘控制
//call
//method: control
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  flag;			//控制的标志 参考streamcontrol.h 定义
	int32u  deviceid;       //设备ID

	int32u  otherflag; //参考streamcontrol.h 定义
	int32u  key; //对应的key值 参考streamcontrol.h 定义
}Type_MeetDoScreenKeyBoardControl, *pType_MeetDoScreenKeyBoardControl;

//媒体播放
//playflag
#define MEDIA_PLAYFLAG_LOOP			0x00000001 //循环播放标志
#define MEDIA_PLAYFLAG_CLIENT		0x00000002 //终端设备 在devnum=0时会判断
#define MEDIA_PLAYFLAG_PROJECTIVE   0x00000004 //投影设备 在devnum=0时会判断
#define MEDIA_PLAYFLAG_NOSELFID     0x00000008 //排除自己的设备ID 在devnum=0时会判断

//停止资源功能
//callback
//type:TYPE_MEET_INTERFACE_STOPPLAY
//method: close
typedef struct
{
	Type_HeaderInfo hdr;

	int   avalableres;//有效资源个数,为0表示全部
	int8u res[32];
}Type_MeetStopResWork, *pType_MeetStopResWork;

//关闭资源功能
//call
//type:TYPE_MEET_INTERFACE_STOPPLAY
//method: close
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  playflag;//播放标志 MEDIA_PLAYFLAG_CLIENT
	int32u  triggeruserval;//参见该文件中的triggeruserval定义

	int   avalableres;//有效资源个数,为0表示全部
	int8u res[32];

	int   devnum;
	//int32u deviceid[devnum];
}Type_MeetDoStopResWork, *pType_MeetDoStopResWork;

//停止触发器
//call
//type:TYPE_MEET_INTERFACE_STOPPLAY
//method: stop
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  playflag;//播放标志 MEDIA_PLAYFLAG_CLIENT
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
	int32u  triggerid;//触发器ID

	int   devnum;
	//int32u deviceid[devnum];
}Type_MeetDoStopTrrigerWork, *pType_MeetDoStopTrrigerWork;

//数据后台回复的错误信息
//callback
//type:TYPE_MEET_INTERFACE_DBSERVERERROR
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  type;
	int32u  method;
	int32u  status;
}Type_MeetDBServerOperError, *pType_MeetDBServerOperError;

//视频播放资源初始化
//call
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: init
typedef struct
{
	Type_HeaderInfo hdr;

	int8u res;//资源索引
	void* parent;					//窗口句柄
	int x, y, w, h;					//资源的坐标和宽高
}Type_MeetInitPlayRes, *pType_MeetInitPlayRes;

//视频播放资源释放
//call
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: destroy
typedef struct
{
	Type_HeaderInfo hdr;

	int8u res;//资源索引
}Type_MeetDestroyPlayRes, *pType_MeetDestroyPlayRes;

//媒体播放通知
//callback
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggerid;//触发器ID
	int32u  createdeviceid;//创建执行该触发器的设备ID
	int32u  mediaid;//播放的媒体id
	int8u   res;//播放所用的资源ID
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
}Type_MeetMediaPlay, *pType_MeetMediaPlay;

//call
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: start
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  playflag;//播放标志

	int     pos;//指定开始播放的百分比位置
	int32u  mediaid;//媒体ID
	int32u  triggeruserval;//触发器标志

	int	   resnum;
	int8u  res[MAX_RES_NUM];
	int    devnum; //播放设备
	//int32u deviceid[devnum];
}Type_MeetDoMediaPlay, *pType_MeetDoMediaPlay;

//设置播放位置
//call
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: move
typedef struct
{
	Type_HeaderInfo hdr;

	int8u   resindex;//资源索引号
	int     pos;//指定开始播放的百分比位置
	int32u  playflag;//播放标志 MEDIA_PLAYFLAG_CLIENT
	int32u  triggeruserval;//参见该文件中的triggeruserval定义

	int    devnum; //播放设备
	//int32u deviceid[devnum];
}Type_MeetDoSetPlayPos, *pType_MeetDoSetPlayPos;

//设置播放暂停 恢复
//call
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: pause play
typedef struct
{
	Type_HeaderInfo hdr;

	int8u   resindex;//资源索引号
	int32u  playflag;//播放标志 MEDIA_PLAYFLAG_CLIENT
	int32u  triggeruserval;//参见该文件中的triggeruserval定义

	int     devnum; //播放设备
	//int32u deviceid[devnum];
}Type_MeetDoPlayControl, *pType_MeetDoPlayControl;

//查询媒体播放
typedef struct
{
	int32u trrigerid;//触发器ID
	int32u mediaid;//媒体ID
}Item_MeetMediaPlayDetailInfo, *pItem_MeetMediaPlayDetailInfo;

//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: query
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetMediaPlayDetailInfo, *pType_MeetMediaPlayDetailInfo;

//文件推送
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: notify
//当为询问模式时，回调函数返回1表示同意播放
typedef struct
{
	Type_HeaderInfo hdr;
	int32u mediaid;
	int32u triggeruserval;//参见该文件中的triggeruserval定义
}Type_FilePush, *pType_FilePush;

//文件推送
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: push
typedef struct
{
	Type_HeaderInfo hdr;

	int32u mediaid;
	int32u triggeruserval;//参见该文件中的triggeruserval定义

	int	   devnum;
	//int32u devid[devnum];
}Type_DoFilePush, *pType_DoFilePush;


#define REQUEST_USERVAL_JOINPLAY	1//userdefval1 有申请加入同屏 播放当前推送发起的文件
#define REQUEST_USERVAL_UPDATEPLAY	2//userdefval1 跟随播放当前文件 需要mediaid==发起推送的meidaid

//音视频文件跟随播放
//type:TYPE_MEET_INTERFACE_MEDIAPLAY
//method: METHOD_MEET_INTERFACE_UPDATE
//当为询问模式时，回调函数返回1表示同意播放
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  mediaid;
	int32u  deviceid;//发起请求的设备ID
	int8u   resindex;//资源索引号
	int8u   fill[3];
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
	int32u  userdefval1;//用户自定义的值
}Type_ReqMediaUpdatePlay, *pType_ReqMediaUpdatePlay;

//流请求
//type:TYPE_MEET_INTERFACE_STREAMPLAY
//method: notify
//当为询问模式时，回调函数返回1表示同意播放
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
	int32u  deviceid;       //发起请求的设备ID
	int32u  memberid;       //发起请求的人员ID
	int8u   substreamindex;       //本设备子通道号
}Type_ReqStreamPush, *pType_ReqStreamPush;

//流请求
//type:TYPE_MEET_INTERFACE_STREAMPLAY
//method: requestpush
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggeruserval;		  //参见该文件中的triggeruserval定义
	int32u  handledeviceid;       //处理该请求的设备ID
	int8u   substreamindex;       //本设备子通道号

	int    devnum; //播放设备
	//int32u deviceid[devnum];
}Type_DoReqStreamPush, *pType_DoReqStreamPush;

//流推送
//type:TYPE_MEET_INTERFACE_STREAMPLAY
//method: notify
//当为询问模式时，回调函数返回1表示同意播放
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
	int32u  deviceid;       //发起请求的设备ID
	int8u   substreamindex; //本设备子通道号

}Type_StreamPush, *pType_StreamPush;

//流推送
//type:TYPE_MEET_INTERFACE_STREAMPLAY
//method: push
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
	int32u  deviceid;       //发起请求的设备ID
	int8u   substreamindex; //本设备子通道号

	int    devnum; //播放设备
	//int32u deviceid[devnum];
}Type_DoStreamPush, *pType_DoStreamPush;

//流播放通知
//callback
//type:TYPE_MEET_INTERFACE_STREAMPLAY
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggerid;//触发器ID
	int32u  createdeviceid;//创建执行该触发器的设备ID
	int32u  deviceid;//流源设备ID
	int32u  subid;//流源设备子通道ID
	int8u   res;//播放所用的资源ID
	int32u  triggeruserval;//参见该文件中的triggeruserval定义
}Type_MeetStreamPlay, *pType_MeetStreamPlay;

//流播放
//call
//method: start
typedef struct
{
	Type_HeaderInfo hdr;

	int32u  deviceid;//设备ID
	int32u  subid;//设备子通道
	int32u  playflag;//播放标志 MEDIA_PLAYFLAG_CLIENT
	int32u  triggeruserval;//参见该文件中的triggeruserval定义

	int	   resnum;
	int8u  res[MAX_RES_NUM];

	int    devnum; //播放设备
	//int32u deviceid[devnum];
}Type_MeetDoStreamPlay, *pType_MeetDoStreamPlay;

//查询流播放
typedef struct
{
	int32u trrigerid;//触发器ID
	int32u  deviceid;//设备ID
	int32u  subid;//设备子通道
}Item_MeetStreamPlayDetailInfo, *pItem_MeetStreamPlayDetailInfo;

//type:TYPE_MEET_INTERFACE_STREAMPLAY
//method: query/
typedef struct
{
	Type_HeaderInfo hdr;
	int32u	 num; //
}Type_MeetStreamPlayDetailInfo, *pType_MeetStreamPlayDetailInfo;

//媒体、流停止播放通知
//callback
//type:TYPE_MEET_INTERFACE_STOPPLAY
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;
	int32u  triggerid;
	int32u  createdeviceid;
	int8u   res;
}Type_MeetStopPlay, *pType_MeetStopPlay;

//查询会议统计
//call
//type:TYPE_MEET_INTERFACE_MEETSTATISTIC
//method: query
typedef struct
{
	Type_HeaderInfo hdr;

	int32u meetingid;//会议ID 为0表示查询当前会议
}Type_MeetDoReqStatistic, *pType_MeetDoReqStatistic;

//返回查询会议统计通知
//callback
//type:TYPE_MEET_INTERFACE_MEETSTATISTIC
//method: notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u meetingid;//会议ID
	char   meetname[DEFAULT_DESCRIBE_LENG];//会议名称
	int32u streamgetcount;//流请求数
	int32u screengetcount;//同屏请求数
	int32u filegetcount;//文件请求数
	int32u chatcount;//交流次数
	int32u servicegetcount;//服务请求数
	int32u whiteboardopencount;//白板发起次数
	int32u whiteboardusecount;//白板交流次数
	int32u votecount;//投票发起次数
	int32u electioncount;//选举发起次数
	int32u questioncount;//问卷调查发起次数
	int32u bulletcount;//公告发起次数
	int64u addtime;//创建时间 秒
}Type_MeetStatisticInfo, *pType_MeetStatisticInfo;

//时间段会议统计
//quartertype
#define MEET_QUERYSTATISTIC_BYMONTH	  1//按月份查询,最多一次可查12个月
#define MEET_QUERYSTATISTIC_BYQUARTER 2//按季度查询,最多一次可查12个季度
#define MEET_QUERYSTATISTIC_BYYEAR	  3//按年查询,最多一次可查12个年


//按时间段查询会议统计
//call
//type:TYPE_MEET_INTERFACE_MEETSTATISTIC
//method: ask
typedef struct
{
	Type_HeaderInfo hdr;
	
	int32u quartertype;//参见 quartertype定义

	//统计时间段
	int16u startyear; //
	int16u startmonth; ////按月查询才有效
	int16u endyear; //
	int16u endmonth; ////按月查询才有效
}Type_QueryQuarterStatistic, *pType_QueryQuarterStatistic;

typedef struct
{
	//统计时间段
	int16u startyear; //
	int16u startmonth; //
	int16u endyear; //
	int16u endmonth; //

	int32u meetingcount;//总会议数

	//总计数
	int32u streamgetcount;//流请求数
	int32u screengetcount;//同屏请求数
	int32u filegetcount;//文件请求数
	int32u chatcount;//交流次数
	int32u servicegetcount;//服务请求数
	int32u whiteboardopencount;//白板发起次数
	int32u whiteboardusecount;//白板交流次数
	int32u votecount;//投票发起次数
	int32u electioncount;//选举发起次数
	int32u questioncount;//问卷调查发起次数
	int32u bulletcount;//公告发起次数

}Item_MeetOneStatistic, *pItem_MeetOneStatistic;

//返回按时间段查询会议统计
//callback
//type:TYPE_MEET_INTERFACE_MEETSTATISTIC
//method: ask
typedef struct
{
	Type_HeaderInfo hdr;

	int32u quartertype;//
	int32u num;
	pItem_MeetOneStatistic pitem;
}Type_MeetQuarterStatisticInfo, *pType_MeetQuarterStatisticInfo;

#ifndef MEET_FACEID_MAINBG
//返回查询界面配置
//会议界面设置
//faceid
#define MEET_FACEID_MAINBG			1  //主界面 png  //仅ID
#define MEET_FACEID_SUBBG			2  //子界面 png  //仅ID
#define MEET_FACEID_LOGO			3  //logo png    //仅ID
#define MEET_FACEID_MEETNAME		4  //会议名称 text
#define MEET_FACEID_MEMBERNAME		5  //参会人名称 text
#define MEET_FACEID_MEMBERCOMPANY	6  //参会人单位 text
#define MEET_FACEID_MEMBERJOB		7  //参会人职业 text
#define MEET_FACEID_SEATNAME		8  //座席名称 text
#define MEET_FACEID_TIMER			9  //日期时间 text
#define MEET_FACEID_COMPANY			10 //单位名称 text
#define MEET_FACEID_SHOWFILE		11 //开会预读文件、视频等  //仅ID
#define MEET_FACEID_COLTDTEXT		12 //公司名称 onytext

#define MEET_FACE_LOGO_GEO		13 //text
#define MEET_FACE_topstatus_GEO		14 //会议状态 text 
#define MEET_FACE_checkin_GEO		15 //进入会议按钮 text
#define MEET_FACE_manage_GEO		16 //进入后台 text
#define MEET_FACE_remark_GEO		17 //备注 text
#define MEET_FACE_role_GEO		18 //角色 text
#define MEET_FACE_ver_GEO		19 //版本 text

#define MEET_FACE_SeatIcoShow_GEO		20 //排位图标是否显示 text

#define MEET_FACE_SeatLayoutShow_top		21 //桌牌显示上  text
#define MEET_FACE_SeatLayoutShow_main		22 //桌牌显示中  text
#define MEET_FACE_SeatLayoutShow_bottom		23 //桌牌显示下  text

#define  MEET_FACE_MEETAGENDAFont         24

#define MEET_FACE_BulletinBK        25  // 会议公告背景  png （仅ID）
#define MEET_FACE_BulletinLogo      26  // 会议公告LOGO  png （仅ID）
#define MEET_FACE_BulletinTitle     27  // 会议公告标题  text
#define MEET_FACE_BulletinContent   28  // 会议公告内容  text
#define MEET_FACE_BulletinBtn       29  // 公告关闭按钮  text


#define MEET_FACEID_PROJECTIVE_MIANBG		101  //投影界面 png
#define MEET_FACEID_PROJECTIVE_LOGO			102  //logo png
#define MEET_FACEID_PROJECTIVE_MEETNAME		103  //会议名称 text
#define MEET_FACEID_PROJECTIVE_SEATNAME		104  //座席名称 text
#define MEET_FACEID_PROJECTIVE_TIMER		105  //日期时间 text
#define MEET_FACEID_PROJECTIVE_COMPANY		106  //单位名称 text
#define MEET_FACEID_PROJECTIVE_SIGNINFO		107  //签到情况 text
#define MEET_FACEID_PROJECTIVE_MEETTIME		108  //会议时间 text
#define MEET_FACEID_PROJECTIVE_COMPANYNAME	109  //公司名称位置 text
#define MEET_FACEID_PROJECTIVE_STATUS		110  //会议状态 text

#define MEET_FACEID_PROJECTIVE_SHOWFILE		111  //开会预读文件、视频等  //仅ID
#define MEET_FACEID_PROJECTIVE_COLTDTEXT	112  //公司名称 onytext


//fontflag
#define MEET_FONTFLAG_BOLD		0x00000001 //加粗
#define MEET_FONTFLAG_LEAN		0x00000002 //倾斜
#define MEET_FONTFLAG_UNDERLINE 0x00000004 //下划线

//flag
#define MEET_FACEFLAG_SHOW			0x00000001 //该位用于表示该项是否可见
#define MEET_FACEFLAG_TEXT			0x00000002 //该位用于表示数据是文本类型,否则为文件ID
#define MEET_FACEFLAG_ONLYTEXT		0x00000004 //该位用于表示数据是纯文本类型
#endif

//文本项
typedef struct
{
	unsigned int faceid;  //界面项ID
	unsigned int flag;	  //属性值

	unsigned short  fontsize;	//字体大小
	unsigned int    color;		//字体rgba颜色
	unsigned short  align;		//对齐 参见本文件的font align flag
	unsigned short  fontflag;	//字体属性
	char	fontname[FONTNAME_LENG];//字体名称

	float lx;//坐标 左上角x  (x * 100 / width)
	float ly;//坐标 左上角y  (y * 100 / height)
	float bx;//坐标 右下角x
	float by;//坐标 右下角y

}Item_FaceTextItemInfo, *pItem_FaceTextItemInfo;

//图片项
typedef struct
{
	unsigned int faceid;  //界面项ID
	unsigned int flag;    //属性值
	unsigned int mediaid; //项值
}Item_FacePictureItemInfo, *pItem_FacePictureItemInfo;

//纯文本项
typedef struct
{
	unsigned int faceid;  //界面项ID
	unsigned int flag;    //属性值
	char		 text[DEFAULT_DESCRIBE_LENG];//文本
}Item_FaceOnlyTextItemInfo, *pItem_FaceOnlyTextItemInfo;

//call
//type:TYPE_MEET_INTERFACE_MEETFACECONFIG
//method: query modify
typedef struct
{
	Type_HeaderInfo hdr;

	int num;
}Type_FaceConfigInfo, *pType_FaceConfigInfo;

//callback
//TYPE_MEET_INTERFACE_MEETFACECONFIG
//method:notify
typedef struct
{
	Type_HeaderInfo hdr;

	int32u opermethod;//本次通知是因为opermethod方法触发的
	int32u id;//如果指定了ID刚表示是对特定ID的操作,为0表示全部
}Type_meetFaceConfigInfo, *pType_meetFaceConfigInfo;

//call
//type:TYPE_MEET_INTERFACE_MEETSENDMAIL
//method: send

#define MEET_MAIL_PATHLEN 260
typedef struct
{
	char filepath[MEET_MAIL_PATHLEN];
}Item_meetMailPathInfo, *pItem_meetMailPathInfo;

//type
#define MEET_MAIL_SENDTYPE_TO  0 //发送
#define MEET_MAIL_SENDTYPE_CC  1 //抄送
#define MEET_MAIL_SENDTYPE_BCC 2 //秘密发送

typedef struct
{
	int32u userid;
	int	   type;//
}Item_meetMailUserInfo, *pItem_meetMailUserInfo;

typedef struct
{
	Type_HeaderInfo hdr;

	const char* subject;//主题
	const char* bodytext;//正文

	//file
	int filenum;
	pItem_meetMailPathInfo pfile;

	//recive member
	int     useridnum;
	pItem_meetMailUserInfo puser;
}Type_SendMailInfo, *pType_SendMailInfo;

//callback
//TYPE_MEET_INTERFACE_MEETSENDMAIL
//method:notify

#define MEET_MAIL_SENDSTATE_OK	  0 //发送成功
#define MEET_MAIL_SENDSTATE_ERROR 1 //发送失败

#define MEET_MAIL_STATEINFO 1024
typedef struct
{
	Type_HeaderInfo hdr;

	int  error;
	char stateinfo[MEET_MAIL_STATEINFO];//邮件发送状态
}Type_meetMailInfo, *pType_meetMailInfo;
#pragma pack(pop)
// #ifdef __cplusplus
// }
// #endif

#endif