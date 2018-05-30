#ifndef LIB_MEETINTREFACE_MODULE_H
#define LIB_MEETINTREFACE_MODULE_H


#ifdef __cplusplus
extern "C" {
#endif

#include "base.h"
#include "meetc/meetinterface_type.h"

	//���ó�����û��������һ��ʵ��
	//��������-1 ����һ��ʵ�������з���1 û��ʵ�����з���0
	int meetcore_checkselfrun();

//��������
#define MEET_PROGRAM_TYPE_MEETCLIENT	 1 //�����ն����
#define MEET_PROGRAM_TYPE_PROJECTIVE	 2 //ͶӰ���
#define MEET_PROGRAM_TYPE_STREAMCAPTURE  3 //���ɼ����
#define MEET_PROGRAM_TYPE_SERVICE		 4 //��ˮ�������
#define MEET_PROGRAM_TYPE_ANDROIDAPP	 5 //android APP
#define MEET_PROGRAM_TYPE_CLIENTDEBUG	 6 //TCP�ͻ��˵���
#define MEET_PROGRAM_TYPE_PUBLISHER	 7 //���鷢�����

	//����ģ��ص����ݽӿ�
	//phdr �ص������� ���ݽṹ�� Type_HeaderInfo + �������ʵ������
	typedef int(*MEETCORE_CB)(pType_HeaderInfo phdr, void* puser);

	// ����ģ����־�ص����ݽӿ�
#define MEETCORE_LOGLEVEL_ERROR     0//����
#define MEETCORE_LOGLEVEL_INFO      1//��Ϣ
#define MEETCORE_LOGLEVEL_COMMON    2//��Ϣ
#define MEETCORE_LOGLEVEL_WARNING   3//����
#define MEETCORE_LOGLEVEL_DEBUG     4//����

	//logflag
#define MEETLOG_FLAG_LOGTOFILE  0x00000001 //���浽��־�ļ�
#define MEETLOG_FLAG_PRINTFOUNT 0x00000002 //�����console

	//loglevel ��־�ȼ�
	//pmsg �ص�������
	typedef void(*MEETCORE_LOGCB)(int loglevel, const char* pmsg, void* puser);

	typedef struct 
	{
		const char* pconfigpathname;//client.ini�ļ�·����
		int			streamnum;//��ʼ������ͨ����

		void*		   puser;//�û�ָ�� �ص������л�ͨ�� void* puser ���ظ�ָ��
		MEETCORE_CB    pfunc;//�ص�����

		int			   blogtofile;//�μ� logflag
		MEETCORE_LOGCB plogfunc;//��־�ص�����

		int			usekeystr;//ʹ�ô���ĳ����ʶ,
		char		keystr[64];//�����ʶ,����Щ�޷���ȡΨһ��ʶ���豸ʹ��

		int			programtype;//�������� �������ֳ�������ʼ���ӿ�����
	}MeetCore_InitParam, *pMeetCore_InitParam;

	//��ʼ������ģ��
	//0��ʾ�ɹ�,-1��ʾʧ��
	int meetcore_init(pMeetCore_InitParam pa);

//oper flag �������ݷ��صĲ�����־
#define MEETCORE_CALL_FLAG_JUSTRETURN		 0 //��ʾ�����ݷ��ظ����÷�
#define MEETCORE_CALL_FLAG_JUSTCALLBACK		 1 //��ʾ������ͨ���ص�����
#define MEETCORE_CALL_FLAG_CALLBACKANDRETURN 2 //��ʾ������ͬʱ���ݵ��ص�ҲҪ��������

	//pdata����
	//pretdata ���ص����� ���ݽṹ�� Type_HeaderInfo + �������ʵ������
	//operflag ָʾ���ݵĲ�����־ �μ�MEETCORE_CALL_FLAG_JUSTRETURN ����
	//���ظ�����ʾ����, 0��ʾû�з���ֵ, ��ֵ��ʾpretdata�Ĵ�С
	int meetcore_Call(pType_HeaderInfo pdata, pType_HeaderInfo* pretdata, int32u operflag);

	//type ��������
	//�����ͷ�meetcore_call ��pretdata��Դ
	void meetcore_free(pType_HeaderInfo pdata);
	
	//��ȡtype ��Ӧ���ַ��� eg:type=TYPE_MEET_INTERFACE_TIME --> "TYPE_MEET_INTERFACE_TIME"
	const char* meetcore_GettypeStr(int32u type);

	//��ȡmethod ��Ӧ���ַ��� eg:method=METHOD_MEET_INTERFACE_NOTIFY --> "METHOD_MEET_INTERFACE_NOTIFY"
	const char* meetcore_GetMethodStr(int32u method);

	//��ȡerror ��Ӧ���ַ��� eg:error=ERROR_MEET_INTERFACE_NOTINIT --> "ERROR_MEET_INTERFACE_NOTINIT"
	const char* meetcore_GetErrorStr(int32u err);

	//��ȡstatus ��Ӧ���ַ��� eg:status=STATUS_ACCESSDENIED --> "STATUS_ACCESSDENIED"
	const char* meetcore_GetStatusMsg(int32u status);

	//�����־,�����������־�ص�����,����־�ص����û����õĻص�����
	void meetcore_printf(int loglevel, const char *pszFmt, ...);

#ifdef __cplusplus
}
#endif


#endif