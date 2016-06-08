package com.warriorfitapp.mobile.youtube;

/**
 * @author Andrii Kovalov
 *         <p/>
 *         Samples:
 *         Thumbnail (480x360 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/0.jpg
 *         Thumbnail (120x90 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/1.jpg
 *         Thumbnail (120x90 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/2.jpg
 *         Thumbnail (120x90 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/3.jpg
 *         Thumbnail (480x360 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/hqdefault.jpg
 *         Thumbnail (320x180 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/mqdefault.jpg
 *         Thumbnail (120x90 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/default.jpg
 *         Thumbnail (640x480 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/sddefault.jpg
 *         Thumbnail (1920x1080 pixels) http://i1.ytimg.com/vi/oB1CUxX1JJE/maxresdefault.jpg
 */
public enum YoutubeThumbnail {
    _0("0"), _1("1"), _2("2"), _3("3"), HQDEFAULT("hqdefault"), MQDEFAULT("mqdefault"), DEFAULT("default"), SDDEFAULT("sddefault"), MAXRESDEFAULT("maxresdefault");

    private String id;

    YoutubeThumbnail(String id) {
        this.id = id;
    }

    public String toURL(String youtubeId) {
        return "http://i.ytimg.com/vi/" + youtubeId + "/" + id + ".jpg";
    }
}
