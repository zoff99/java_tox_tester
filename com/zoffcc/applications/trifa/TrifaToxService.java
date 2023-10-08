/**
 * [TRIfA], Java part of Tox Reference Implementation for Android
 * Copyright (C) 2017 Zoff <zoff@zoff.cc>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package com.zoffcc.applications.trifa;

import static com.zoffcc.applications.trifa.MainActivity.get_my_toxid;
import static com.zoffcc.applications.trifa.MainActivity.tox_self_get_name;
import static com.zoffcc.applications.trifa.MainActivity.tox_self_get_name_size;
import static com.zoffcc.applications.trifa.MainActivity.tox_self_get_status_message;
import static com.zoffcc.applications.trifa.MainActivity.tox_self_get_status_message_size;
import static com.zoffcc.applications.trifa.MainActivity.tox_self_set_name;
import static com.zoffcc.applications.trifa.MainActivity.tox_self_set_status_message;
import static com.zoffcc.applications.trifa.MainActivity.tox_friend_add;
import static com.zoffcc.applications.trifa.TRIFAGlobals.global_my_name;
import static com.zoffcc.applications.trifa.TRIFAGlobals.global_my_status_message;
import static com.zoffcc.applications.trifa.TRIFAGlobals.global_my_toxid;
import static com.zoffcc.applications.trifa.TRIFAGlobals.bootstrapping;
import static com.zoffcc.applications.trifa.TRIFAGlobals.ECHOBOT_TOXID;
import static com.zoffcc.applications.trifa.TRIFAGlobals.GROUPBOT_TOXID;

// import static com.zoffcc.applications.trifa.TRIFAGlobals.*;
// import static com.zoffcc.applications.trifa.MainActivity.*;



public class TrifaToxService
{
    static int ONGOING_NOTIFICATION_ID = 1030;
    static final String TAG = "trifa.ToxService";
    static Thread ToxServiceThread = null;
    static boolean stop_me = false;
    static boolean is_tox_started = false;
    static boolean global_toxid_text_set = false;
    static boolean TOX_SERVICE_STARTED = false;

    static class Log
    {
        public static void i(String tag, String message)
        {
            System.out.println("" + tag + ":" + message + "");
        }
    }

    void tox_thread_start_fg()
    {
        Log.i(TAG, "tox_thread_start_fg");

        ToxServiceThread = new Thread()
        {
            @Override
            public void run()
            {

                // ------ correct startup order ------
                boolean old_is_tox_started = is_tox_started;
                Log.i(TAG, "is_tox_started:==============================");
                Log.i(TAG, "is_tox_started=" + is_tox_started);
                Log.i(TAG, "is_tox_started:==============================");

                is_tox_started = true;

                if (!old_is_tox_started)
                {
                    MainActivity.init_tox_callbacks();
                    MainActivity.update_savedata_file_wrapper(MainActivity.password_hash);
                }
                // ------ correct startup order ------

                // TODO --------
                String my_tox_id_local = get_my_toxid();
                global_my_toxid = my_tox_id_local;
                if (tox_self_get_name_size() > 0)
                {
                    global_my_name = tox_self_get_name().substring(0, (int) tox_self_get_name_size());
                    Log.i(TAG, "AAA:003:" + global_my_name + " size=" + tox_self_get_name_size());
                }
                else
                {
                    tox_self_set_name("TRIfA " + my_tox_id_local.substring(my_tox_id_local.length() - 5, my_tox_id_local.length()));
                    global_my_name = ("TRIfA " + my_tox_id_local.substring(my_tox_id_local.length() - 5, my_tox_id_local.length()));
                    Log.i(TAG, "AAA:005");
                }

                if (tox_self_get_status_message_size() > 0)
                {
                    global_my_status_message = tox_self_get_status_message().substring(0, (int) tox_self_get_status_message_size());
                    Log.i(TAG, "AAA:008:" + global_my_status_message + " size=" + tox_self_get_status_message_size());
                }
                else
                {
                    tox_self_set_status_message("this is TRIfA");
                    global_my_status_message = "this is TRIfA";
                    Log.i(TAG, "AAA:010");
                }
                Log.i(TAG, "AAA:011");

                MainActivity.update_savedata_file_wrapper(MainActivity.password_hash);

                MainActivity.friends = MainActivity.tox_self_get_friend_list();
                Log.i(TAG, "loading_friend:number_of_friends=" + MainActivity.friends.length);

                int fc = 0;
                boolean exists_in_db = false;

                for (fc = 0; fc < MainActivity.friends.length; fc++)
                {

                }


                // --------------- bootstrap ---------------
                // --------------- bootstrap ---------------
                // --------------- bootstrap ---------------
                if (!old_is_tox_started)
                {
                    bootstrapping = true;
                    Log.i(TAG, "bootrapping:set to true");

                    // ----- UDP ------
                    Log.i(TAG, "bootstrap_single:res=" + MainActivity.bootstrap_single_wrapper("tox.verdict.gg", 33445, "1C5293AEF2114717547B39DA8EA6F1E331E5E358B35F9B6B5F19317911C5F976"));
                    Log.i(TAG, "bootstrap_single:res=" + MainActivity.bootstrap_single_wrapper("tox.initramfs.io", 33445, "3F0A45A268367C1BEA652F258C85F4A66DA76BCAA667A49E770BCC4917AB6A25"));
                    Log.i(TAG, "bootstrap_single:res=" + MainActivity.bootstrap_single_wrapper("205.185.115.131", 53, "3091C6BEB2A993F1C6300C16549FABA67098FF3D62C6D253828B531470B53D68"));
                    Log.i(TAG, "bootstrap_single:res=" + MainActivity.bootstrap_single_wrapper("2a03:b0c0:3:d0::ac:5001", 33445, "CD133B521159541FB1D326DE9850F5E56A6C724B5B8E5EB5CD8D950408E95707"));
                    // ----- UDP ------
                    //
                    // ----- TCP ------
                    Log.i(TAG, "add_tcp_relay_single:res=" + MainActivity.add_tcp_relay_single_wrapper("tox.verdict.gg", 33445, "1C5293AEF2114717547B39DA8EA6F1E331E5E358B35F9B6B5F19317911C5F976"));
                    Log.i(TAG, "add_tcp_relay_single:res=" + MainActivity.add_tcp_relay_single_wrapper("tox.initramfs.io", 33445, "3F0A45A268367C1BEA652F258C85F4A66DA76BCAA667A49E770BCC4917AB6A25"));
                    Log.i(TAG, "add_tcp_relay_single:res=" + MainActivity.add_tcp_relay_single_wrapper("205.185.115.131", 443, "3091C6BEB2A993F1C6300C16549FABA67098FF3D62C6D253828B531470B53D68"));
                    // ----- TCP ------
                }

                // --------------- bootstrap ---------------
                // --------------- bootstrap ---------------
                // --------------- bootstrap ---------------

                long tox_iteration_interval_ms = MainActivity.tox_iteration_interval();
                Log.i(TAG, "tox_iteration_interval_ms=" + tox_iteration_interval_ms);

                MainActivity.tox_iterate();

                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                while (!stop_me)
                {
                    try
                    {
                        if (tox_iteration_interval_ms < 3)
                        {
                            Log.i(TAG, "tox_iterate:(tox_iteration_interval_ms < 2ms!!):" + tox_iteration_interval_ms + "ms");
                            Thread.sleep(3);
                        }
                        else
                        {
                            // Log.i(TAG, "(tox_iteration_interval_ms):" + tox_iteration_interval_ms + "ms");
                            Thread.sleep(tox_iteration_interval_ms);
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    // Log.i(TAG, "tox_iterate:--START--");
                    long s_time = System.currentTimeMillis();
                    MainActivity.tox_iterate();
                    if (s_time + 4000 < System.currentTimeMillis())
                    {
                        tox_iteration_interval_ms = MainActivity.tox_iteration_interval();
                        Log.i(TAG, "tox_iterate:--END--:took" + (long) (((float) (s_time - System.currentTimeMillis()) / 1000f)) + "s, new inerval=" + tox_iteration_interval_ms + "ms");
                    }
                }
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------
                // ------- MAIN TOX LOOP ---------------------------------------------------------------


                try
                {
                    Thread.sleep(100); // wait a bit, for "something" to finish up in the native code
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    MainActivity.tox_kill();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    Thread.sleep(100); // wait a bit, for "something" to finish up in the native code
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        };

        ToxServiceThread.start();
    }

    // ------------------------------


    // --------------- JNI ---------------
    // --------------- JNI ---------------
    // --------------- JNI ---------------
    static void logger(int level, String text)
    {
        Log.i(TAG, text);
    }

    static String safe_string(byte[] in)
    {
        // Log.i(TAG, "safe_string:in=" + in);
        String out = "";

        try
        {
            out = new String(in, "UTF-8");  // Best way to decode using "UTF-8"
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "safe_string:EE:" + e.getMessage());
            try
            {
                out = new String(in);
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
                Log.i(TAG, "safe_string:EE2:" + e2.getMessage());
            }
        }

        // Log.i(TAG, "safe_string:out=" + out);
        return out;
    }
    // --------------- JNI ---------------
    // --------------- JNI ---------------
    // --------------- JNI ---------------
}
