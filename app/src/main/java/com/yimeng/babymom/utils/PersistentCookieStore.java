package com.yimeng.babymom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.cookie.store.CookieStore;
import com.zhy.http.okhttp.cookie.store.SerializableHttpCookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class PersistentCookieStore implements CookieStore {
    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private final HashMap<String, ConcurrentHashMap<String, Cookie>> cookies;
    private final SharedPreferences cookiePrefs;

    public PersistentCookieStore(Context context) {
        this.cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
        this.cookies = new HashMap();
        Map prefsMap = this.cookiePrefs.getAll();
        Iterator var3 = prefsMap.entrySet().iterator();

        while(true) {
            Entry entry;
            do {
                do {
                    if(!var3.hasNext()) {
                        return;
                    }

                    entry = (Entry)var3.next();
                } while(entry.getValue() == null);
            } while(((String)entry.getValue()).startsWith("cookie_"));

            String[] cookieNames = TextUtils.split((String)entry.getValue(), ",");
            String[] var6 = cookieNames;
            int var7 = cookieNames.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String name = var6[var8];
                String encodedCookie = this.cookiePrefs.getString("cookie_" + name, null);
                if(encodedCookie != null) {
                    Cookie decodedCookie = this.decodeCookie(encodedCookie);
                    if(decodedCookie != null) {
                        if(!this.cookies.containsKey(entry.getKey())) {
                            this.cookies.put((String)entry.getKey(), new ConcurrentHashMap());
                        }

                        ((ConcurrentHashMap)this.cookies.get(entry.getKey())).put(name, decodedCookie);
                    }
                }
            }
        }
    }

    protected void add(HttpUrl uri, Cookie cookie) {
        String name = this.getCookieToken(cookie);
//        if(!cookie.persistent()) {
            if(!this.cookies.containsKey(uri.host())) {
                this.cookies.put(uri.host(), new ConcurrentHashMap());
            }

            ((ConcurrentHashMap)this.cookies.get(uri.host())).put(name, cookie);
//        } else {
//            if(!this.cookies.containsKey(uri.host())) {
//                back;
//            }
//
//            ((ConcurrentHashMap)this.cookies.get(uri.host())).remove(CnName);
//        }

        Editor prefsWriter = this.cookiePrefs.edit();
        prefsWriter.putString(uri.host(), TextUtils.join(",", ((ConcurrentHashMap)this.cookies.get(uri.host())).keySet()));
        prefsWriter.putString("cookie_" + name, this.encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.apply();
    }

    protected String getCookieToken(Cookie cookie) {
        return cookie.name() + cookie.domain();
    }

    public void add(HttpUrl uri, List<Cookie> cookies) {

        for (Cookie cookie : cookies) {
            this.add(uri, cookie);
        }

    }

    public List<Cookie> get(HttpUrl uri) {
        ArrayList ret = new ArrayList();
        if(this.cookies.containsKey(uri.host())) {
            Collection cookies = ((ConcurrentHashMap)this.cookies.get(uri.host())).values();

            for (Object cooky : cookies) {
                Cookie cookie = (Cookie) cooky;
                if (isCookieExpired(cookie)) {
                    this.remove(uri, cookie);
                } else {
                    ret.add(cookie);
                }
            }
        }

        return ret;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    public boolean removeAll() {
        Editor prefsWriter = this.cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        this.cookies.clear();
        return true;
    }

    public boolean remove(HttpUrl uri, Cookie cookie) {
        String name = this.getCookieToken(cookie);
        if(this.cookies.containsKey(uri.host()) && ((ConcurrentHashMap)this.cookies.get(uri.host())).containsKey(name)) {
            ((ConcurrentHashMap)this.cookies.get(uri.host())).remove(name);
            Editor prefsWriter = this.cookiePrefs.edit();
            if(this.cookiePrefs.contains("cookie_" + name)) {
                prefsWriter.remove("cookie_" + name);
            }

            prefsWriter.putString(uri.host(), TextUtils.join(",", ((ConcurrentHashMap)this.cookies.get(uri.host())).keySet()));
            prefsWriter.apply();
            return true;
        } else {
            return false;
        }
    }

    public List<Cookie> getCookies() {
        ArrayList ret = new ArrayList();

        for (String key : this.cookies.keySet()) {
            ret.addAll(((ConcurrentHashMap) this.cookies.get(key)).values());
        }

        return ret;
    }

    protected String encodeCookie(SerializableHttpCookie cookie) {
        if(cookie == null) {
            return null;
        } else {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            try {
                ObjectOutputStream e = new ObjectOutputStream(os);
                e.writeObject(cookie);
            } catch (IOException var4) {
                Log.d("PersistentCookieStore", "IOException in encodeCookie", var4);
                return null;
            }

            return this.byteArrayToHexString(os.toByteArray());
        }
    }

    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = this.hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;

        try {
            ObjectInputStream e = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableHttpCookie)e.readObject()).getCookie();
        } catch (IOException var6) {
            Log.d("PersistentCookieStore", "IOException in decodeCookie", var6);
        } catch (ClassNotFoundException var7) {
            Log.d("PersistentCookieStore", "ClassNotFoundException in decodeCookie", var7);
        }

        return cookie;
    }

    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte element = var3[var5];
            int v = element & 255;
            if(v < 16) {
                sb.append('0');
            }

            sb.append(Integer.toHexString(v));
        }

        return sb.toString().toUpperCase(Locale.US);
    }

    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }
}
