/**
 * [TRIfA], Java part of Tox Reference Implementation for Android
 * Copyright (C) 2017 - 2021 Zoff <zoff@zoff.cc>
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

//  ==================================================
//  compile with:
//   javac com/zoffcc/applications/trifa/MainActivity.java
//   javac com/zoffcc/applications/trifa/ToxVars.java
//   javac com/zoffcc/applications/trifa/TRIFAGlobals.java
//   javac com/zoffcc/applications/trifa/TrifaToxService.java
//  ==================================================


import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import static com.zoffcc.applications.trifa.TRIFAGlobals.bootstrapping;
import static com.zoffcc.applications.trifa.ToxVars.TOX_HASH_LENGTH;
import static com.zoffcc.applications.trifa.ToxVars.TOX_PUBLIC_KEY_SIZE;

public class MainActivity
{
    private static final String TAG = "trifa.MainActivity";
    private static final String Version = "1.0.1";
    // --------- global config ---------
    // --------- global config ---------
    // --------- global config ---------
    final static boolean CTOXCORE_NATIVE_LOGGING = true; // set "false" for release builds
    final static boolean ORMA_TRACE = false; // set "false" for release builds
    final static boolean DB_ENCRYPT = true; // set "true" always!
    final static boolean VFS_ENCRYPT = true; // set "true" always!
    // --------- global config ---------
    // --------- global config ---------
    // --------- global config ---------

    static TrifaToxService tox_service_fg = null;
    static boolean native_lib_loaded = false;
    static long[] friends = null;
	static String app_files_directory = "./";
    static String password_hash = "pass";
    static Semaphore semaphore_tox_savedata = new Semaphore(1);
    final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    static String to_add_toxid = null;
    static int friend_last_connection_status = 0;
    final static String send_this_message = "Hello!\nHow are you doing? Tox is a nice messaging tool.\n😁😆😅🤣😂🙂🙃😉😘\nLet's meet later, what do you say?";
    final static String BOT_MY_NAME = "Bob Bobovic 😎☂️";
    final static String BOT_MY_STATUS_MSG = "I like dogs";
    final static String image_file_name = "tombaker.webp";
    final static long image_file_size = new java.io.File(image_file_name).length();
    static boolean send_done = false;

    static class Log
    {
        public static void i(String tag, String message)
        {
            message = message.replace("\r","").replace("\n","");
            System.out.println("" + tag + ":" + message + "");
        }
    }

    public static void main(String[] args)
    {
        // Prints "Hello, World" in the terminal window.
        System.out.println("Version:" + Version);

        TrifaToxService.TOX_SERVICE_STARTED = false;
        bootstrapping = false;
        Log.i(TAG, "java.library.path:" + System.getProperty("java.library.path"));

        Log.i(TAG, "loaded:c-toxcore:v" + tox_version_major() + "." + tox_version_minor() + "." + tox_version_patch());
        Log.i(TAG, "loaded:jni-c-toxcore:v" + jnictoxcore_version());


        tox_service_fg = new TrifaToxService();

        if (!TrifaToxService.TOX_SERVICE_STARTED)
        {
			int PREF__udp_enabled = 1;
			int PREF__orbot_enabled_to_int = 0;
			String ORBOT_PROXY_HOST = "";
			long ORBOT_PROXY_PORT = 0;
            int PREF__local_discovery_enabled = 1;
            int PREF__ipv6_enabled = 1;
            int PREF__force_udp_only = 0;
            int PREF__ngc_video_bitrate = 500;
            int PREF__ngc_video_max_quantizer = 51;
            int PREF__ngc_audio_bitrate = 8000;
            int PREF__ngc_audio_samplerate = 48000;
            int PREF__ngc_audio_channels = 1;

			app_files_directory = "./";

            init(app_files_directory, PREF__udp_enabled, PREF__local_discovery_enabled, PREF__orbot_enabled_to_int,
                 ORBOT_PROXY_HOST, ORBOT_PROXY_PORT, password_hash, PREF__ipv6_enabled, PREF__force_udp_only,
                 PREF__ngc_video_bitrate,
                 PREF__ngc_video_max_quantizer,
                 PREF__ngc_audio_bitrate, PREF__ngc_audio_samplerate,
                 PREF__ngc_audio_channels);

            tox_service_fg.tox_thread_start_fg();
        }

        String my_tox_id_temp = get_my_toxid();
        Log.i(TAG, "MyToxID:" + my_tox_id_temp);

        if (args.length > 0)
        {
            try
            {
                to_add_toxid = args[0];
                Log.i(TAG, "will try to add friend: " + to_add_toxid);
            }
            catch (Exception e)
            {
            }
        }

        tox_self_set_name(BOT_MY_NAME);
        tox_self_set_status_message(BOT_MY_STATUS_MSG);

    }


    static
    {
        try
        {
            System.loadLibrary("jni-c-toxcore");
            native_lib_loaded = true;
            Log.i(TAG, "successfully loaded native library");
        }
        catch (java.lang.UnsatisfiedLinkError e)
        {
            native_lib_loaded = false;
            Log.i(TAG, "loadLibrary jni-c-toxcore failed!");
            e.printStackTrace();
            System.exit(4);
        }
    }


    // -------- native methods --------
    // -------- native methods --------
    // -------- native methods --------
    public static native void init(String data_dir, int udp_enabled, int local_discovery_enabled, int orbot_enabled, String orbot_host, long orbot_port, String tox_encrypt_passphrase_hash, int enable_ipv6, int force_udp_only_mode, int ngc_video_bitrate, int max_quantizer, int ngc_audio_bitrate, int ngc_audio_sampling_rate, int ngc_audio_channel_count);

    public native String getNativeLibAPI();

    public static native String getNativeLibGITHASH();

    public static native String getNativeLibTOXGITHASH();

    public static native void update_savedata_file(String tox_encrypt_passphrase_hash);

    public static native String get_my_toxid();

    public static native int add_tcp_relay_single(String ip, String key_hex, long port);

    public static native int bootstrap_single(String ip, String key_hex, long port);

    public static native int tox_self_get_connection_status();

    public static native void init_tox_callbacks();

    public static native long tox_iteration_interval();

    public static native long tox_iterate();

    // ----------- TRIfA internal -----------
    public static native int jni_iterate_group_audio(int delta_new, int want_ms_output);

    public static native int jni_iterate_videocall_audio(int delta_new, int want_ms_output, int channels, int sample_rate, int send_emtpy_buffer);

    public static native void crgb2yuv(java.nio.ByteBuffer rgba_buf, java.nio.ByteBuffer yuv_buf, int w_yuv, int h_yuv, int w_rgba, int h_rgba);

    public static native void tox_set_do_not_sync_av(int do_not_sync_av);

    public static native void tox_set_onion_active(int active);
    // ----------- TRIfA internal -----------

    public static native long tox_kill();

    public static native void exit();

    public static native long tox_friend_send_message(long friendnum, int a_TOX_MESSAGE_TYPE, String message);

    public static native long tox_version_major();

    public static native long tox_version_minor();

    public static native long tox_version_patch();

    public static native String jnictoxcore_version();

    public static native String libavutil_version();

    public static native String libopus_version();

    public static native String libsodium_version();

    public static native long tox_max_filename_length();

    public static native long tox_file_id_length();

    public static native long tox_max_message_length();

    public static native long tox_friend_add(String toxid_str, String message);

    public static native long tox_friend_add_norequest(String public_key_str);

    public static native long tox_self_get_friend_list_size();

    public static native void tox_self_set_nospam(long nospam); // this actually needs an "uint32_t" which is an unsigned 32bit integer value

    public static native long tox_self_get_nospam(); // this actually returns an "uint32_t" which is an unsigned 32bit integer value

    public static native long tox_friend_by_public_key(String friend_public_key_string);

    public static native String tox_friend_get_public_key(long friend_number);

    public static native long tox_friend_get_capabilities(long friend_number);

    public static native long[] tox_self_get_friend_list();

    public static native int tox_self_set_name(String name);

    public static native int tox_self_set_status_message(String status_message);

    public static native void tox_self_set_status(int a_TOX_USER_STATUS);

    public static native int tox_self_set_typing(long friend_number, int typing);

    public static native int tox_friend_get_connection_status(long friend_number);

    public static native int tox_friend_delete(long friend_number);

    public static native String tox_self_get_name();

    public static native long tox_self_get_name_size();

    public static native long tox_self_get_status_message_size();

    public static native String tox_self_get_status_message();

    public static native int tox_friend_send_lossless_packet(long friend_number, byte[] data, int data_length);

    public static native int tox_file_control(long friend_number, long file_number, int a_TOX_FILE_CONTROL);

    public static native int tox_hash(ByteBuffer hash_buffer, ByteBuffer data_buffer, long data_length);

    public static native int tox_file_seek(long friend_number, long file_number, long position);

    public static native int tox_file_get_file_id(long friend_number, long file_number, ByteBuffer file_id_buffer);

    public static native long tox_file_send(long friend_number, long kind, long file_size, ByteBuffer file_id_buffer, String file_name, long filename_length);

    public static native int tox_file_send_chunk(long friend_number, long file_number, long position, ByteBuffer data_buffer, long data_length);

    // --------------- Message V2 -------------
    // --------------- Message V2 -------------
    // --------------- Message V2 -------------
    public static native long tox_messagev2_size(long text_length, long type, long alter_type);

    public static native int tox_messagev2_wrap(long text_length, long type, long alter_type, ByteBuffer message_text_buffer, long ts_sec, long ts_ms, ByteBuffer raw_message_buffer, ByteBuffer msgid_buffer);

    public static native int tox_messagev2_get_message_id(ByteBuffer raw_message_buffer, ByteBuffer msgid_buffer);

    public static native long tox_messagev2_get_ts_sec(ByteBuffer raw_message_buffer);

    public static native long tox_messagev2_get_ts_ms(ByteBuffer raw_message_buffer);

    public static native long tox_messagev2_get_message_text(ByteBuffer raw_message_buffer, long raw_message_len, int is_alter_msg, long alter_type, ByteBuffer message_text_buffer);

    public static native String tox_messagev2_get_sync_message_pubkey(ByteBuffer raw_message_buffer);

    public static native long tox_messagev2_get_sync_message_type(ByteBuffer raw_message_buffer);

    public static native int tox_util_friend_send_msg_receipt_v2(long friend_number, long ts_sec, ByteBuffer msgid_buffer);

    public static native long tox_util_friend_send_message_v2(long friend_number, int type, long ts_sec, String message, long length, ByteBuffer raw_message_back_buffer, ByteBuffer raw_message_back_buffer_length, ByteBuffer msgid_back_buffer);

    public static native int tox_util_friend_resend_message_v2(long friend_number, ByteBuffer raw_message_buffer, long raw_msg_len);
    // --------------- Message V2 -------------
    // --------------- Message V2 -------------
    // --------------- Message V2 -------------

    // --------------- Message V3 -------------
    // --------------- Message V3 -------------
    // --------------- Message V3 -------------
    public static native int tox_messagev3_get_new_message_id(ByteBuffer hash_buffer);

    public static native long tox_messagev3_friend_send_message(long friendnum, int a_TOX_MESSAGE_TYPE, String message, ByteBuffer mag_hash, long timestamp);
    // --------------- Message V3 -------------
    // --------------- Message V3 -------------
    // --------------- Message V3 -------------

    // --------------- Conference -------------
    // --------------- Conference -------------
    // --------------- Conference -------------

    public static native long tox_conference_join(long friend_number, ByteBuffer cookie_buffer, long cookie_length);

    public static native long tox_conference_peer_count(long conference_number);

    public static native long tox_conference_peer_get_name_size(long conference_number, long peer_number);

    public static native String tox_conference_peer_get_name(long conference_number, long peer_number);

    public static native String tox_conference_peer_get_public_key(long conference_number, long peer_number);

    public static native long tox_conference_offline_peer_count(long conference_number);

    public static native long tox_conference_offline_peer_get_name_size(long conference_number, long offline_peer_number);

    public static native String tox_conference_offline_peer_get_name(long conference_number, long offline_peer_number);

    public static native String tox_conference_offline_peer_get_public_key(long conference_number, long offline_peer_number);

    public static native long tox_conference_offline_peer_get_last_active(long conference_number, long offline_peer_number);

    public static native int tox_conference_peer_number_is_ours(long conference_number, long peer_number);

    public static native long tox_conference_get_title_size(long conference_number);

    public static native String tox_conference_get_title(long conference_number);

    public static native int tox_conference_get_type(long conference_number);

    public static native int tox_conference_send_message(long conference_number, int a_TOX_MESSAGE_TYPE, String message);

    public static native int tox_conference_delete(long conference_number);

    public static native long tox_conference_get_chatlist_size();

    public static native long[] tox_conference_get_chatlist();

    public static native int tox_conference_get_id(long conference_number, ByteBuffer cookie_buffer);

    public static native int tox_conference_new();

    public static native int tox_conference_invite(long friend_number, long conference_number);

    public static native int tox_conference_set_title(long conference_number, String title);
    // --------------- Conference -------------
    // --------------- Conference -------------
    // --------------- Conference -------------

    // --------------- new Groups -------------
    // --------------- new Groups -------------
    // --------------- new Groups -------------

    /**
     * Creates a new group chat.
     * <p>
     * This function creates a new group chat object and adds it to the chats array.
     * <p>
     * The caller of this function has Founder role privileges.
     * <p>
     * The client should initiate its peer list with self info after calling this function, as
     * the peer_join callback will not be triggered.
     *
     * @param a_TOX_GROUP_PRIVACY_STATE The privacy state of the group. If this is set to TOX_GROUP_PRIVACY_STATE_PUBLIC,
     *                                  the group will attempt to announce itself to the DHT and anyone with the Chat ID may join.
     *                                  Otherwise a friend invite will be required to join the group.
     * @param group_name                The name of the group. The name must be non-NULL.
     * @param my_peer_name              The name of the peer creating the group.
     * @return group_number on success, UINT32_MAX on failure.
     */
    public static native long tox_group_new(int a_TOX_GROUP_PRIVACY_STATE, String group_name, String my_peer_name);

    /**
     * Joins a group chat with specified Chat ID.
     * <p>
     * This function creates a new group chat object, adds it to the chats array, and sends
     * a DHT announcement to find peers in the group associated with chat_id. Once a peer has been
     * found a join attempt will be initiated.
     *
     * @param chat_id_buffer The Chat ID of the group you wish to join. This must be TOX_GROUP_CHAT_ID_SIZE bytes.
     * @param password       The password required to join the group. Set to NULL if no password is required.
     * @param my_peer_name   The name of the peer joining the group.
     * @return group_number on success, UINT32_MAX on failure.
     */
    public static native long tox_group_join(ByteBuffer chat_id_buffer, long chat_id_length, String my_peer_name, String password);

    public static native int tox_group_leave(long group_number, String part_message);

    public static native long tox_group_self_get_peer_id(long group_number);

    public static native int tox_group_self_set_name(long group_number, String my_peer_name);

    public static native String tox_group_self_get_public_key(long group_number);

    public static native int tox_group_self_get_role(long group_number);

    public static native int tox_group_peer_get_role(long group_number, long peer_id);

    public static native int tox_group_get_chat_id(long group_number, ByteBuffer chat_id_buffer);

    public static native long tox_group_get_number_groups();

    public static native long[] tox_group_get_grouplist();

    public static native long tox_group_peer_count(long group_number);

    public static native int tox_group_get_peer_limit(long group_number);

    public static native int tox_group_founder_set_peer_limit(long group_number, int max_peers);

    public static native long tox_group_offline_peer_count(long group_number);

    public static native long[] tox_group_get_peerlist(long group_number);

    public static native long tox_group_by_chat_id(ByteBuffer chat_id_buffer);

    public static native int tox_group_get_privacy_state(long group_number);

    public static native int tox_group_mod_kick_peer(long group_number, long peer_id);

    public static native int tox_group_mod_set_role(long group_number, long peer_id, int a_Tox_Group_Role);

    public static native String tox_group_peer_get_public_key(long group_number, long peer_id);

    public static native long tox_group_peer_by_public_key(long group_number, String peer_public_key_string);

    public static native String tox_group_peer_get_name(long group_number, long peer_id);

    public static native String tox_group_get_name(long group_number);

    public static native String tox_group_get_topic(long group_number);

    public static native int tox_group_peer_get_connection_status(long group_number, long peer_id);

    public static native int tox_group_invite_friend(long group_number, long friend_number);

    public static native int tox_group_is_connected(long group_number);

    public static native int tox_group_reconnect(long group_number);

    public static native int tox_group_send_custom_packet(long group_number, int lossless, byte[] data, int data_length);

    public static native int tox_group_send_custom_private_packet(long group_number, long peer_id, int lossless, byte[] data, int data_length);

    /**
     * Send a text chat message to the group.
     * <p>
     * This function creates a group message packet and pushes it into the send
     * queue.
     * <p>
     * The message length may not exceed TOX_MAX_MESSAGE_LENGTH. Larger messages
     * must be split by the client and sent as separate messages. Other clients can
     * then reassemble the fragments. Messages may not be empty.
     *
     * @param group_number       The group number of the group the message is intended for.
     * @param a_TOX_MESSAGE_TYPE Message type (normal, action, ...).
     * @param message            A non-NULL pointer to the first element of a byte array
     *                           containing the message text.
     * @return message_id on success. return < 0 on error.
     */
    public static native long tox_group_send_message(long group_number, int a_TOX_MESSAGE_TYPE, String message);

    /**
     * Send a text chat message to the specified peer in the specified group.
     * <p>
     * This function creates a group private message packet and pushes it into the send
     * queue.
     * <p>
     * The message length may not exceed TOX_MAX_MESSAGE_LENGTH. Larger messages
     * must be split by the client and sent as separate messages. Other clients can
     * then reassemble the fragments. Messages may not be empty.
     *
     * @param group_number The group number of the group the message is intended for.
     * @param peer_id      The ID of the peer the message is intended for.
     * @param message      A non-NULL pointer to the first element of a byte array
     *                     containing the message text.
     * @return true on success.
     */
    public static native int tox_group_send_private_message(long group_number, long peer_id, int a_TOX_MESSAGE_TYPE, String message);

    /**
     * Send a text chat message to the specified peer in the specified group.
     * <p>
     * This function creates a group private message packet and pushes it into the send
     * queue.
     * <p>
     * The message length may not exceed TOX_MAX_MESSAGE_LENGTH. Larger messages
     * must be split by the client and sent as separate messages. Other clients can
     * then reassemble the fragments. Messages may not be empty.
     *
     * @param group_number           The group number of the group the message is intended for.
     * @param peer_public_key_string A memory region of at least TOX_PUBLIC_KEY_SIZE bytes of the peer the
     *                               message is intended for. If this parameter is NULL, this function will return false.
     * @param message                A non-NULL pointer to the first element of a byte array
     *                               containing the message text.
     * @return 0 on success. return < 0 on error.
     */
    public static native int tox_group_send_private_message_by_peerpubkey(long group_number, String peer_public_key_string, int a_TOX_MESSAGE_TYPE, String message);

    /**
     * Accept an invite to a group chat that the client previously received from a friend. The invite
     * is only valid while the inviter is present in the group.
     *
     * @param invite_data_buffer The invite data received from the `group_invite` event.
     * @param my_peer_name       The name of the peer joining the group.
     * @param password           The password required to join the group. Set to NULL if no password is required.
     * @return the group_number on success, UINT32_MAX on failure.
     */
    public static native long tox_group_invite_accept(long friend_number, ByteBuffer invite_data_buffer, long invite_data_length, String my_peer_name, String password);
    // --------------- new Groups -------------
    // --------------- new Groups -------------
    // --------------- new Groups -------------


    // --------------- AV -------------
    // --------------- AV -------------
    // --------------- AV -------------
    public static native int toxav_answer(long friendnum, long audio_bit_rate, long video_bit_rate);

    public static native long toxav_iteration_interval();

    public static native int toxav_call(long friendnum, long audio_bit_rate, long video_bit_rate);

    public static native int toxav_bit_rate_set(long friendnum, long audio_bit_rate, long video_bit_rate);

    public static native int toxav_call_control(long friendnum, int a_TOXAV_CALL_CONTROL);

    public static native int toxav_video_send_frame_uv_reversed(long friendnum, int frame_width_px, int frame_height_px);

    public static native int toxav_video_send_frame(long friendnum, int frame_width_px, int frame_height_px);

    public static native int toxav_video_send_frame_age(long friendnum, int frame_width_px, int frame_height_px, int age_ms);

    public static native int toxav_video_send_frame_h264(long friendnum, int frame_width_px, int frame_height_px, long data_len);

    public static native int toxav_video_send_frame_h264_age(long friendnum, int frame_width_px, int frame_height_px, long data_len, int age_ms);

    public static native int toxav_option_set(long friendnum, long a_TOXAV_OPTIONS_OPTION, long value);

    public static native void set_av_call_status(int status);

    public static native void set_audio_play_volume_percent(int volume_percent);

    // buffer is for incoming video (call)
    public static native long set_JNI_video_buffer(java.nio.ByteBuffer buffer, int frame_width_px, int frame_height_px);

    // buffer2 is for sending video (call)
    public static native void set_JNI_video_buffer2(java.nio.ByteBuffer buffer2, int frame_width_px, int frame_height_px);

    // audio_buffer is for sending audio (group and call)
    public static native void set_JNI_audio_buffer(java.nio.ByteBuffer audio_buffer);

    // audio_buffer2 is for incoming audio (group and call)
    public static native void set_JNI_audio_buffer2(java.nio.ByteBuffer audio_buffer2);

    /**
     * Send an audio frame to a friend.
     * <p>
     * The expected format of the PCM data is: [s1c1][s1c2][...][s2c1][s2c2][...]...
     * Meaning: sample 1 for channel 1, sample 1 for channel 2, ...
     * For mono audio, this has no meaning, every sample is subsequent. For stereo,
     * this means the expected format is LRLRLR... with samples for left and right
     * alternating.
     *
     * @param friend_number The friend number of the friend to which to send an
     *                      audio frame.
     * @param sample_count  Number of samples in this frame. Valid numbers here are
     *                      ((sample rate) * (audio length) / 1000), where audio length can be
     *                      2.5, 5, 10, 20, 40 or 60 millseconds.
     * @param channels      Number of audio channels. Supported values are 1 and 2.
     * @param sampling_rate Audio sampling rate used in this frame. Valid sampling
     *                      rates are 8000, 12000, 16000, 24000, or 48000.
     */
    public static native int toxav_audio_send_frame(long friend_number, long sample_count, int channels, long sampling_rate);
    // --------------- AV -------------
    // --------------- AV -------------
    // --------------- AV -------------

    // -------- native methods --------
    // -------- native methods --------
    // -------- native methods --------

    // -------- called by AV native methods --------
    // -------- called by AV native methods --------
    // -------- called by AV native methods --------
    // --------------- AV -------------
    // --------------- AV -------------
    // --------------- AV -------------

    // -------- native methods --------
    // -------- native methods --------
    // -------- native methods --------

    // -------- called by AV native methods --------
    // -------- called by AV native methods --------
    // -------- called by AV native methods --------

    static void android_toxav_callback_call_cb_method(long friend_number, int audio_enabled, int video_enabled)
    {
    }

    static void android_toxav_callback_video_receive_frame_cb_method(long friend_number, long frame_width_px, long frame_height_px, long ystride, long ustride, long vstride)
    {
    }

    static void android_toxav_callback_call_state_cb_method(long friend_number, int a_TOXAV_FRIEND_CALL_STATE)
    {
    }

    static void android_toxav_callback_bit_rate_status_cb_method(long friend_number, long audio_bit_rate, long video_bit_rate)
    {
    }

    static void android_toxav_callback_audio_receive_frame_cb_method(long friend_number, long sample_count, int channels, long sampling_rate)
    {
    }

    static void android_toxav_callback_audio_receive_frame_pts_cb_method(long friend_number, long sample_count, int channels, long sampling_rate, long pts)
    {
        android_toxav_callback_audio_receive_frame_cb_method(friend_number, sample_count, channels, sampling_rate);
    }

    static void android_toxav_callback_video_receive_frame_pts_cb_method(long friend_number, long frame_width_px, long frame_height_px, long ystride, long ustride, long vstride, long pts)
    {
        android_toxav_callback_video_receive_frame_cb_method(friend_number, frame_width_px, frame_height_px, ystride,
                                                             ustride, vstride);
    }

    static void android_toxav_callback_video_receive_frame_h264_cb_method(long friend_number, long buf_size)
    {
    }

    static void android_toxav_callback_group_audio_receive_frame_cb_method(long conference_number, long peer_number, long sample_count, int channels, long sampling_rate)
    {
    }

    static void android_toxav_callback_call_comm_cb_method(long friend_number, long a_TOXAV_CALL_COMM_INFO, long comm_number)
    {
    }

    // -------- called by AV native methods --------
    // -------- called by AV native methods --------
    // -------- called by AV native methods --------


    // -------- called by native methods --------
    // -------- called by native methods --------
    // -------- called by native methods --------

    static void android_tox_callback_self_connection_status_cb_method(int a_TOX_CONNECTION)
    {
		Log.i(TAG, "self_connection_status:status:" + a_TOX_CONNECTION);
        try
        {
            if (a_TOX_CONNECTION != 0)
            {
                if (to_add_toxid != null)
                {
                    tox_friend_add(to_add_toxid, "Tox Tester");
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    static void android_tox_callback_friend_name_cb_method(long friend_number, String friend_name, long length)
    {
    }

    static void android_tox_callback_friend_status_message_cb_method(long friend_number, String status_message, long length)
    {
    }

    static void android_tox_callback_friend_lossless_packet_cb_method(long friend_number, byte[] data, long length)
    {
    }

    static void android_tox_callback_friend_status_cb_method(long friend_number, int a_TOX_USER_STATUS)
    {
    }

    static void android_tox_callback_friend_connection_status_cb_method(long friend_number, int a_TOX_CONNECTION)
    {
		Log.i(TAG, "friend_connection_status:friend:" + friend_number + " status:" + a_TOX_CONNECTION);
        // HINT: send only the first time. in case connection is flakey.
        if (send_done) { return; }
        try
        {
            send_done = true;
            // HINT: send the message only when then friend is coming online, not when changing from TCP to UDP or vice versa.
            if ((a_TOX_CONNECTION != 0) && (friend_last_connection_status == 0))
            {
                if (to_add_toxid != null)
                {
                    tox_friend_send_message(friend_number, 0, send_this_message);
                    send_tombaker(friend_number);
                }
            }
        }
        catch (Exception e)
        {
        }
        friend_last_connection_status = a_TOX_CONNECTION;
    }

    static void android_tox_callback_friend_typing_cb_method(long friend_number, final int typing)
    {
    }

    static void android_tox_callback_friend_read_receipt_cb_method(long friend_number, long message_id)
    {
    }

    static void android_tox_callback_friend_request_cb_method(String friend_public_key, String friend_request_message, long length)
    {
		Log.i(TAG, "friend_request:friend:" + friend_public_key + " friend request message:" + friend_request_message);

        try
        {
            final String friend_public_key__final = friend_public_key.substring(0, TOX_PUBLIC_KEY_SIZE * 2);
            long friendnum = tox_friend_add_norequest(friend_public_key__final);
        }
        catch (Exception e)
        {
        }

        MainActivity.update_savedata_file_wrapper(MainActivity.password_hash);
    }

    static void android_tox_callback_friend_message_cb_method(long friend_number, int message_type, String friend_message, long length, byte[] msgV3hash_bin, long message_timestamp)
    {
		Log.i(TAG, "friend_message:friendnum:" + friend_number + " message:" + friend_message);
    }

    static void android_tox_callback_friend_message_v2_cb_method(long friend_number, String friend_message, long length, long ts_sec, long ts_ms, byte[] raw_message, long raw_message_length)
    {
		Log.i(TAG, "friend_message_v2:friendnum:" + friend_number + " message:" + friend_message);

            ByteBuffer raw_message_buf = ByteBuffer.allocateDirect((int) raw_message_length);
            raw_message_buf.put(raw_message, 0, (int) raw_message_length);
            ByteBuffer msg_id_buffer = ByteBuffer.allocateDirect(TOX_HASH_LENGTH);
            tox_messagev2_get_message_id(raw_message_buf, msg_id_buffer);
            // long ts_sec2 = tox_messagev2_get_ts_sec(raw_message_buf);
            // long ts_ms2 = tox_messagev2_get_ts_ms(raw_message_buf);

            ByteBufferCompat msg_id_buffer_compat = new ByteBufferCompat(msg_id_buffer);
            String msg_id_as_hex_string = bytesToHex(msg_id_buffer_compat.array(), msg_id_buffer_compat.arrayOffset(),
                                                     msg_id_buffer_compat.limit());

            long pin_timestamp = System.currentTimeMillis();
            send_friend_msg_receipt_v2_wrapper(friend_number, 1, msg_id_buffer,
                                                (pin_timestamp / 1000));

    }

    static void android_tox_callback_friend_sync_message_v2_cb_method(long friend_number, long ts_sec, long ts_ms, byte[] raw_message, long raw_message_length, byte[] raw_data, long raw_data_length)
    {
    }

    static void sync_messagev2_send(long friend_number, byte[] raw_data, long raw_data_length, ByteBuffer raw_message_buf_wrapped, ByteBuffer msg_id_buffer, String real_sender_as_hex_string)
    {
    }

    static void android_tox_callback_friend_read_receipt_message_v2_cb_method(final long friend_number, long ts_sec, byte[] msg_id)
    {
    }

    static void android_tox_callback_file_recv_control_cb_method(long friend_number, long file_number, int a_TOX_FILE_CONTROL)
    {
    }

    static void android_tox_callback_file_chunk_request_cb_method(long friend_number, long file_number, long position, long length)
    {
        Log.i(TAG, "android_tox_callback_file_chunk_request_cb_method: fn=" + friend_number + " filenum="
                + file_number + " pos=" + position + " len=" + length);
        if (length == 0L)
        {
            ByteBuffer chunk = ByteBuffer.allocateDirect(1);
            int res = tox_file_send_chunk(friend_number, file_number, position, chunk, 0);
        }
        else
        {
            try
            {
                final byte[] bytes_chunck = new byte[(int) length];
                final java.io.FileInputStream fis = new java.io.FileInputStream(new java.io.File(image_file_name));
                fis.getChannel().position(position);
                final int actually_read = fis.read(bytes_chunck, 0, (int) length);
                Log.i(TAG, "android_tox_callback_file_chunk_request_cb_method: actually_read=" + actually_read);
                try
                {
                    fis.close();
                }
                catch (Exception e2)
                {
                }
                final ByteBuffer file_chunk = ByteBuffer.allocateDirect((int) length);
                file_chunk.put(bytes_chunck);
                tox_file_send_chunk(friend_number, file_number, position, file_chunk, length);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    static void android_tox_callback_file_recv_cb_method(long friend_number, long file_number, int a_TOX_FILE_KIND, long file_size, String filename, long filename_length)
    {
    }

    static void android_tox_callback_file_recv_chunk_cb_method(long friend_number, long file_number, long position, byte[] data, long length)
    {
    }

    static void android_tox_log_cb_method(int a_TOX_LOG_LEVEL, String file, long line, String function, String message)
    {
        if (CTOXCORE_NATIVE_LOGGING)
        {
            Log.i(TAG, "C-TOXCORE:" + ToxVars.TOX_LOG_LEVEL.value_str(a_TOX_LOG_LEVEL) + ":file=" + file + ":linenum=" +
                       line + ":func=" + function + ":msg=" + message);
        }
    }

    // -------- called by native methods --------
    // -------- called by native methods --------
    // -------- called by native methods --------

    // -------- called by native Conference methods --------
    // -------- called by native Conference methods --------
    // -------- called by native Conference methods --------

    static void android_tox_callback_conference_invite_cb_method(long friend_number, int a_TOX_CONFERENCE_TYPE, byte[] cookie_buffer, long cookie_length)
    {
    }

    static void android_tox_callback_conference_connected_cb_method(long conference_number)
    {
    }

    static void android_tox_callback_conference_message_cb_method(long conference_number, long peer_number, int a_TOX_MESSAGE_TYPE, String message_orig, long length)
    {
    }

    static void android_tox_callback_conference_title_cb_method(long conference_number, long peer_number, String title, long title_length)
    {
    }

    static void android_tox_callback_conference_peer_name_cb_method(long conference_number, long peer_number, String name, long name_length)
    {
    }

    static void android_tox_callback_conference_peer_list_changed_cb_method(long conference_number)
    {
    }

    static void android_tox_callback_conference_namelist_change_cb_method(long conference_number, long peer_number, int a_TOX_CONFERENCE_STATE_CHANGE)
    {
    }

    // -------- called by native Conference methods --------
    // -------- called by native Conference methods --------
    // -------- called by native Conference methods --------

    // -------- called by native new Group methods --------
    // -------- called by native new Group methods --------
    // -------- called by native new Group methods --------

    static void android_tox_callback_group_message_cb_method(long group_number, long peer_id, int a_TOX_MESSAGE_TYPE, String message_orig, long length, long message_id)
    {
    }

    static void android_tox_callback_group_private_message_cb_method(long group_number, long peer_id, int a_TOX_MESSAGE_TYPE, String message_orig, long length)
    {
    }

    static void android_tox_callback_group_privacy_state_cb_method(long group_number, final int a_TOX_GROUP_PRIVACY_STATE)
    {
    }

    static void android_tox_callback_group_invite_cb_method(long friend_number, final byte[] invite_data, final long invite_data_length, String group_name)
    {
    }

    static void android_tox_callback_group_peer_join_cb_method(long group_number, long peer_id)
    {
    }

    static void android_tox_callback_group_peer_exit_cb_method(long group_number, long peer_id, int a_Tox_Group_Exit_Type)
    {
    }

    static void android_tox_callback_group_peer_name_cb_method(long group_number, long peer_id)
    {
    }

    static void android_tox_callback_group_join_fail_cb_method(long group_number, int a_Tox_Group_Join_Fail)
    {
    }

    static void android_tox_callback_group_self_join_cb_method(long group_number)
    {
    }

    static void android_tox_callback_group_moderation_cb_method(long group_number, long source_peer_id, long target_peer_id, int a_Tox_Group_Mod_Event)
    {
    }

    static void android_tox_callback_group_connection_status_cb_method(long group_number, int a_TOX_GROUP_CONNECTION_STATUS)
    {
    }

    static void android_tox_callback_group_topic_cb_method(long group_number, long peer_id, String topic, long topic_length)
    {
    }

    static void android_tox_callback_group_custom_packet_cb_method(long group_number, long peer_id, final byte[] data, long length)
    {
    }

    static void android_tox_callback_group_custom_private_packet_cb_method(long group_number, long peer_id, final byte[] data, long length)
    {
    }


    // -------- called by native Conference methods --------
    // -------- called by native Conference methods --------
    // -------- called by native Conference methods --------

    static void send_friend_msg_receipt_v2_wrapper(final long friend_number, final int msg_type, final ByteBuffer msg_id_buffer, long t_sec_receipt) {
        // (msg_type == 1) msgV2 direct message
        // (msg_type == 2) msgV2 relay message
        // (msg_type == 3) msgV2 group confirm msg received message
        // (msg_type == 4) msgV2 confirm unknown received message
        if (msg_type == 1) {
            // send message receipt v2
            tox_util_friend_send_msg_receipt_v2(friend_number, t_sec_receipt, msg_id_buffer);
        } else if (msg_type == 2) {
            tox_util_friend_send_msg_receipt_v2(friend_number, t_sec_receipt,
                    msg_id_buffer);
        } else if (msg_type == 3) {
            // send message receipt v2
            tox_util_friend_send_msg_receipt_v2(friend_number, t_sec_receipt, msg_id_buffer);
        } else if (msg_type == 4) {
            // send message receipt v2 for unknown message
            tox_util_friend_send_msg_receipt_v2(friend_number, t_sec_receipt, msg_id_buffer);
        }
    }

    static String bytesToHex(byte[] bytes, int start, int len)
    {
        char[] hexChars = new char[(len) * 2];
        // System.out.println("blen=" + (len));

        for (int j = start; j < (start + len); j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[(j - start) * 2] = hexArray[v >>> 4];
            hexChars[(j - start) * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    static void update_savedata_file_wrapper(String password_hash_2)
    {
        try
        {
            MainActivity.semaphore_tox_savedata.acquire();
            long start_timestamp = System.currentTimeMillis();
            MainActivity.update_savedata_file(password_hash_2);
            long end_timestamp = System.currentTimeMillis();
            MainActivity.semaphore_tox_savedata.release();
            Log.i(TAG,
                  "update_savedata_file() took:" + (((float) (end_timestamp - start_timestamp)) / 1000f) + "s");
        }
        catch (InterruptedException e)
        {
            MainActivity.semaphore_tox_savedata.release();
            e.printStackTrace();
        }
    }

    static int add_tcp_relay_single_wrapper(String ip, long port, String key_hex)
    {
        return add_tcp_relay_single(ip, key_hex, port);
    }

    static int bootstrap_single_wrapper(String ip, long port, String key_hex)
    {
        return bootstrap_single(ip, key_hex, port);
    }
    
    static void send_tombaker(long friend_number)
    {
        final Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    // HINT: wait a bit after friend comes online, before starting to send a file
                    Thread.sleep(1);
                    ByteBuffer file_id_buffer = ByteBuffer.allocateDirect(ToxVars.TOX_FILE_ID_LENGTH);
                    tox_messagev3_get_new_message_id(file_id_buffer);

                    Log.i(TAG, "start to send file");
                    tox_file_send(friend_number,
                        ToxVars.TOX_FILE_KIND.TOX_FILE_KIND_DATA.value,
                        image_file_size,
                        file_id_buffer,
                        image_file_name,
                        image_file_name.length()
                        );
                }
                catch(Exception e)
                {
                }
            }
        };
        t.start();
    }
}

