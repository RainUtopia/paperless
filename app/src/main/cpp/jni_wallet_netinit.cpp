#include <jni.h>
#include <wallet_net.h>
#include <malloc.h>
#include <string.h>
#include "jni_wallet_netinit.h"
#include "meetc/meetinterface_type.h"
#include "meetc/InterFaceCorePBModule.h"
#include "meetc/androidcapturevideo.h"
// c call java method
static const char* main_interface_class_path_name = "com/wind/myapplication/NativeUtil";
int wallet_netinit_register_native_methods(JNIEnv* env)
{
    static JNINativeMethod native_methods[] =
	{
			{ "Init_walletSys", "([B)I",  (void*) Init_walletSys},
			{ "call_method", "(II[B)[B",  (void*) jni_call},
			{ "InitAndCapture", "(II)I", (void*)jni_AndroidDevice_initcapture },
			{ "call", "(I[B)I", (void*)jni_AndroidDevice_call },
	};

	if (register_native_methods(env, main_interface_class_path_name, native_methods, NELEM(native_methods)) == -1)
	{
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

typedef struct InternalGlobalParam
{
	JavaVM *	 javavm;
	jobject 	 thiz;
	jclass 		 clssJNINet;

	jmethodID mCallBack_DataProc;
	jmethodID mDoCaptureOper;
}InternalGlobalParam, *pInternalGlobalParam;

static pInternalGlobalParam g_pinternalparam = NULL;

/* functions*/
pInternalGlobalParam GetGlobalParam()
{
	return g_pinternalparam;
}

JNIEnv *Adapter_GetEnv()
{
	int status;
	JNIEnv *envnow = NULL;

	if(g_pinternalparam == NULL)
		return NULL;

	status = g_pinternalparam->javavm->GetEnv((void **) &envnow, JNI_VERSION_1_6);
	if (status != JNI_OK)
	{
		status = g_pinternalparam->javavm->AttachCurrentThread(&envnow, NULL);
		if (status < 0) {
			LOGI("Adapter_GetEnv get status2 return null");
			return NULL;
		}
	}
	return envnow;
}

int jni_initforjava(JNIEnv* env, jobject obj)
{
	int ret;
	jclass clss;
	do
	{
		LOGI("jni_initforjava call %d", __LINE__);
		if (g_pinternalparam)
			return 0;

		g_pinternalparam = (pInternalGlobalParam) malloc(
				sizeof(InternalGlobalParam));
		if (g_pinternalparam == NULL)
		{
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}
		memset(g_pinternalparam, 0, sizeof(InternalGlobalParam));
		ret = env->GetJavaVM(&g_pinternalparam->javavm);
		if (ret)
		{
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}

		g_pinternalparam->thiz = env->NewGlobalRef(obj);
		if (g_pinternalparam->thiz == NULL)
		{
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}

		clss = env->FindClass(main_interface_class_path_name);
		if (clss == NULL)
		{
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}
		g_pinternalparam->clssJNINet = (jclass) env->NewGlobalRef(clss);
		if (g_pinternalparam->clssJNINet == NULL)
		{
			env->DeleteLocalRef(clss);
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}
		env->DeleteLocalRef(clss);

		g_pinternalparam->mCallBack_DataProc = env->GetMethodID(
				g_pinternalparam->clssJNINet, "callback_method", "(II[BI)I");
		if (g_pinternalparam->mCallBack_DataProc == NULL)
		{
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}
		g_pinternalparam->mDoCaptureOper = env->GetMethodID(g_pinternalparam->clssJNINet,
													 "callback", "(II)I");
		if (g_pinternalparam->mDoCaptureOper == NULL)
		{
			LOGI("jni_initforjava %d", __LINE__);
			break;
		}
		ret = 0;
	} while (0);

	LOGI("jni_initforjava return %d, retcode:%d", __LINE__, ret);
	return ret;
}

void CBLogInfo(int loglevel, const char* pmsg, void* puser)
{
	LOGI("demolog [%d] %s", loglevel, pmsg);
}

int CallbackFunc(int32u type, int32u method, void* pdata, int datalen, void* puser)
{
	if(datalen <= 0)
		return 0;
	
	/*if (type != TYPE_MEET_INTERFACE_TIME && type != TYPE_MEET_INTERFACE_MEDIAPLAYPOSINFO)
	{
		switch (type)
		{
		default:
			LOGI("demoCb:%d:%s, %d:%s!\n", type, meetcore_GettypeStr(type), method, meetcore_GetMethodStr(method));
			break;
		}
	}*/

	int status, battach = 0;
	JNIEnv *env = NULL;

	status = g_pinternalparam->javavm->GetEnv((void **) &env, JNI_VERSION_1_6);
	if (status != JNI_OK)
	{
		status = g_pinternalparam->javavm->AttachCurrentThread(&env, NULL);
		if (status < 0)
		{
			LOGI("Adapter_GetEnv get status2 return null");
			return 0;
		}
		battach = 1;
	}

    /*if(datalen > 4)
    {
        char* ptmppp = (char*)pdata;
        LOGI("logprintf:%d,%d datalen: %d 0x%02x%02x%02x%02x!\n", type, method, datalen, ptmppp[datalen - 4], ptmppp[datalen - 3], ptmppp[datalen - 2], ptmppp[datalen - 1]);
    }*/

	//JNIEnv*		env = Adapter_GetEnv();
	jbyteArray barray = env->NewByteArray(datalen);
	env->SetByteArrayRegion(barray, 0, datalen, (const jbyte*) pdata);
	env->CallIntMethod(g_pinternalparam->thiz, g_pinternalparam->mCallBack_DataProc, (jint)type, (jint)method, barray, (jint)datalen);
	env->DeleteLocalRef(barray);

	if(battach)
		g_pinternalparam->javavm->DetachCurrentThread();

    //LOGI("logprintf:%d,%d datalen: %d finish!\n", type, method, datalen);
	return 0;
}

jbyteArray jni_call(JNIEnv *env, jobject thiz, jint type, jint method, jbyteArray pdata)
{
	int ret = -1;
	jbyte *jBuf = 0;
    jint length = 0;

    //LOGI("195 jni_call :type:%d method:%d ",type,method);
    		if(NULL != pdata)
    		{
    		        jBuf = env->GetByteArrayElements(pdata, JNI_FALSE);
            		length = env->GetArrayLength(pdata);
    		}
        void* pretdata = NULL;
	    int   retdatalen = 0;
        //LOGI("203 jni_call :type:%d method:%d length:%d ",type,method, length);
		ret = meetPB_call(type, method, jBuf, length, &pretdata, &retdatalen, 0);
		if(jBuf)
		{
		    //只要非0 就可以进入方
		    //释放
		    env->ReleaseByteArrayElements(pdata, jBuf, 0);
		}
		if(ret < ERROR_MEET_INTERFACE_NULL || retdatalen <= 0)
		{
		    LOGI("jni_call failed:type:%d method:%d  ret:%d retdatalen:%d ",type,method, ret, retdatalen);
			return NULL;
		}
        jbyteArray barray = env->NewByteArray(retdatalen);
		env->SetByteArrayRegion(barray, 0, retdatalen,(const jbyte*) pretdata);
		meetPB_free(pretdata, retdatalen);
	return barray;
}

int Init_walletSys(JNIEnv *env, jobject thiz, jbyteArray pdata)
{
	int ret = -1;

	do
	{
        LOGI("%s: %d ", __FUNCTION__, __LINE__);
		if (g_pinternalparam)
			return 0;

        LOGI("%s: %d ", __FUNCTION__, __LINE__);
		if(0 != jni_initforjava(env, thiz))
			break;

        LOGI("%s: %d ", __FUNCTION__, __LINE__);
		jbyte *jBuf = env->GetByteArrayElements(pdata, JNI_FALSE);
		jint length = env->GetArrayLength(pdata);

		MeetPBCore_CallBack cb = { 0 };
		cb.pfunc = CallbackFunc;
		cb.plogfunc = CBLogInfo;
		SetmeetPB_callbackFunction(&cb);
		ret = meetPB_call(/*Pb_TYPE_MEET_INTERFACE_INITENV, Pb_METHOD_MEET_INTERFACE_START*/0,0, jBuf, length, 0, 0, 0);
		env->ReleaseByteArrayElements(pdata, jBuf, 0);

        LOGI("%s: %d ", __FUNCTION__, __LINE__);
		if(ret == ERROR_MEET_INTERFACE_NULL)
        {
            LOGI("%s: %d, ret:%d ", __FUNCTION__, __LINE__, ret);
            ret = 0;
        }
        LOGI("%s: %d ", __FUNCTION__, __LINE__);
	}while(0);
    LOGI("%s: %d ", __FUNCTION__, __LINE__);
	return ret;
}

void InternalAndroidDevice_LogCB(int channelstart, int level, char* pmsg)
{
    LOGI("InternalAndroidDevice_LogCB %s!", pmsg);
}

int InternalAndroidDevice_GetInfoCB(int channelstart, int oper)
{
    int status, battach = 0, vals = 0;
    JNIEnv *env = NULL;

    LOGI("InternalAndroidDevice_GetInfoCB %d", __LINE__);
    status = g_pinternalparam->javavm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (status != JNI_OK)
    {
        status = g_pinternalparam->javavm->AttachCurrentThread(&env, NULL);
        if (status < 0)
        {
            LOGI("InternalAndroidDevice_GetInfoCB %d", __LINE__);
            return 0;
        }
        battach = 1;
    }
    LOGI("InternalAndroidDevice_GetInfoCB %d", __LINE__);
    vals = env->CallIntMethod(g_pinternalparam->thiz, g_pinternalparam->mDoCaptureOper, (jint)channelstart, (jint)oper);

    if(battach)
        g_pinternalparam->javavm->DetachCurrentThread();

    return vals;
}

//android device capture
int jni_AndroidDevice_initcapture(JNIEnv *env, jobject thiz, int type, int channelstart)
{
    LOGI("------------------------------------------------jni_AndroidDevice_initcapture line:%d!\n", __LINE__);
    SetAndroidDeviceLogCallBack(InternalAndroidDevice_LogCB);
    SetAndroidDeviceGetInfoCallBack(InternalAndroidDevice_GetInfoCB);
    if(AndroidDevice_initcapture(type, channelstart) == 0)
    {
        LOGI("----------------------------------------------------------jni_AndroidDevice_initcapture type: %d, channel:%d!\n", type, channelstart);
        return 0;
    }

    LOGI("------------------------------------------------jni_AndroidDevice_initcapture line:%d erro!\n", __LINE__);
    return -1;
}

int jni_AndroidDevice_call(JNIEnv *env, jobject thiz, int channelstart,
						   jbyteArray pdata)
{
	jbyte *jBuf = env->GetByteArrayElements(pdata, JNI_FALSE);
	jint length = env->GetArrayLength(pdata);
	AndroidDevice_call(channelstart, (char*)jBuf, length);
	env->ReleaseByteArrayElements(pdata, jBuf, 0);
	return 0;
}