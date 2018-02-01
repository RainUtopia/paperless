
#ifndef _WALLET_BASE_MACROPUBLIC_H
#define _WALLET_BASE_MACROPUBLIC_H

#define MAX_RES_NUM 6 //本地最大资源数

#define NET_EXCHANGE_TYPE 0x1e //数据库后台点对点传输类型

#define RES_RECORDVIDEOSTREAM_INDEX  0 //视频流索引
#define RES_SCREENSTREAM_INDEX		 1 //屏幕流索引

#define NETINT32DATA_ATTRIBID				47 //标识当前NETBLOBDATA_ATTRIBID的二进制数据是发送给那个设备的寄存器ID
#define MCSERVER_NETBLOBDATA_ATTRIBID		48 //后台服务器   二进制数据使用的寄存器ID
#define COMMONCLIENT_NETBLOBDATA_ATTRIBID   49 //普通会议终端 二进制数据使用的寄存器ID
#define COMMONCLIENT_EXCHANGE_NETBLOBDATA_ATTRIBID   48 //普通会议终端 之间中转二进制数据使用的寄存器ID ,注:数据头使用服务器的应答头

#define  USE_DEVICE_EXCHANGE  1 //启用设备点对点交换数据

#define MEET_SUPPORTOLDANDNEW 0 //为0表示强制匹配会议使用的设备类型

#define MEETPUSHSTREAM_FLAG_MIC		   0x00000001 //音频采集流
#define MEETPUSHSTREAM_FLAG_SCREEN	   0x00000002 //屏幕流
#define MEETPUSHSTREAM_FLAG_USB	       0x00000004 //usb流
#define MEETPUSHSTREAM_FLAG_HKIPC      0x00000008 //usb流
#endif
