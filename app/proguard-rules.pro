#防止inline
-dontoptimize
-dontpreverify
-dontshrink
#ksoap--xmlpullparser
-keep class org.xmlpull.v1.** {*;}
-dontwarn org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.XmlSerializer
#jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

#==============huanxin===========================
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**

#libs/jar
-libraryjars libs/BlueToothFHR-release-dev-dev_64_1.2.5.aar
-libraryjars libs/fetalheart-release-dev-dev_64_1.2.5.aar
-libraryjars libs/fhr_chart-release-dev-dev_64_1.2.5.aar
#-libraryjars libs/ksoap2-android-assembly-3.1.0-jar-with-dependencies.jar
#-libraryjars libs/MobCommons-2016.1201.1839.jar
#-libraryjars libs/MobTools-2016.1201.1839.jar
#-libraryjars libs/okhttputils-2_6_1.jar
#-libraryjars libs/SMSSDK-2.1.3.aar
#SMSSDK
-keep class cn.smssdk.**{*;}
-keep class com.mob.**{*;}

-dontwarn com.mob.**
-dontwarn cn.smssdk.**

#picasso,okhttp,okio
-keep class com.squareup.picasso.** {*;}
-keep public class org.codehaus.**
-keep public class java.nio.**

-dontwarn org.codehaus
-dontwarn java.nio
-dontwarn com.squareup.**
-dontwarn okio.**
#保留行号
-keepattributes SourceFile,LineNumberTable

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

#HOTFIX
#基线包使用，生成mapping.txt
#-printmapping mapping.txt
#生成的mapping.txt在app/buidl/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}

