package com.lennonwoo.rubber.data.model.remote;

import java.util.List;

public class ArtistInfo {

    /**
     * href : https://api.spotify.com/v1/search?query=Adele&offset=0&limit=1&type=artist
     * items : [{"external_urls":{"spotify":"https://open.spotify.com/artist/4dpARuHxo51G3z768sgnrY"},"followers":{"href":null,"total":4711740},"genres":["pop"],"href":"https://api.spotify.com/v1/artists/4dpARuHxo51G3z768sgnrY","id":"4dpARuHxo51G3z768sgnrY","images":[{"height":1000,"url":"https://i.scdn.co/image/ccbe7b4fef679f821988c78dbd4734471834e3d9","width":1000},{"height":640,"url":"https://i.scdn.co/image/f8737f6fda048b45efe91f81c2bda2b601ae689c","width":640},{"height":200,"url":"https://i.scdn.co/image/df070ad127f62d682596e515ac69d5bef56e0897","width":200},{"height":64,"url":"https://i.scdn.co/image/cbbdfb209cc38b2999b1882f42ee642555316313","width":64}],"name":"Adele","popularity":84,"type":"artist","uri":"spotify:artist:4dpARuHxo51G3z768sgnrY"}]
     * limit : 1
     * next : https://api.spotify.com/v1/search?query=Adele&offset=1&limit=1&type=artist
     * offset : 0
     * previous : null
     * total : 172
     */

    private ArtistsBean artists;

    public ArtistsBean getArtists() {
        return artists;
    }

    public void setArtists(ArtistsBean artists) {
        this.artists = artists;
    }

    public static class ArtistsBean {
        private String href;
        private int limit;
        private String next;
        private int offset;
        private Object previous;
        private int total;
        /**
         * external_urls : {"spotify":"https://open.spotify.com/artist/4dpARuHxo51G3z768sgnrY"}
         * followers : {"href":null,"total":4711740}
         * genres : ["pop"]
         * href : https://api.spotify.com/v1/artists/4dpARuHxo51G3z768sgnrY
         * id : 4dpARuHxo51G3z768sgnrY
         * images : [{"height":1000,"url":"https://i.scdn.co/image/ccbe7b4fef679f821988c78dbd4734471834e3d9","width":1000},{"height":640,"url":"https://i.scdn.co/image/f8737f6fda048b45efe91f81c2bda2b601ae689c","width":640},{"height":200,"url":"https://i.scdn.co/image/df070ad127f62d682596e515ac69d5bef56e0897","width":200},{"height":64,"url":"https://i.scdn.co/image/cbbdfb209cc38b2999b1882f42ee642555316313","width":64}]
         * name : Adele
         * popularity : 84
         * type : artist
         * uri : spotify:artist:4dpARuHxo51G3z768sgnrY
         */

        private List<ItemsBean> items;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public Object getPrevious() {
            return previous;
        }

        public void setPrevious(Object previous) {
            this.previous = previous;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean {
            /**
             * spotify : https://open.spotify.com/artist/4dpARuHxo51G3z768sgnrY
             */

            private ExternalUrlsBean external_urls;
            /**
             * href : null
             * total : 4711740
             */

            private FollowersBean followers;
            private String href;
            private String id;
            private String name;
            private int popularity;
            private String type;
            private String uri;
            private List<String> genres;
            /**
             * height : 1000
             * url : https://i.scdn.co/image/ccbe7b4fef679f821988c78dbd4734471834e3d9
             * width : 1000
             */

            private List<ImagesBean> images;

            public ExternalUrlsBean getExternal_urls() {
                return external_urls;
            }

            public void setExternal_urls(ExternalUrlsBean external_urls) {
                this.external_urls = external_urls;
            }

            public FollowersBean getFollowers() {
                return followers;
            }

            public void setFollowers(FollowersBean followers) {
                this.followers = followers;
            }

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getPopularity() {
                return popularity;
            }

            public void setPopularity(int popularity) {
                this.popularity = popularity;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUri() {
                return uri;
            }

            public void setUri(String uri) {
                this.uri = uri;
            }

            public List<String> getGenres() {
                return genres;
            }

            public void setGenres(List<String> genres) {
                this.genres = genres;
            }

            public List<ImagesBean> getImages() {
                return images;
            }

            public void setImages(List<ImagesBean> images) {
                this.images = images;
            }

            public static class ExternalUrlsBean {
                private String spotify;

                public String getSpotify() {
                    return spotify;
                }

                public void setSpotify(String spotify) {
                    this.spotify = spotify;
                }
            }

            public static class FollowersBean {
                private Object href;
                private int total;

                public Object getHref() {
                    return href;
                }

                public void setHref(Object href) {
                    this.href = href;
                }

                public int getTotal() {
                    return total;
                }

                public void setTotal(int total) {
                    this.total = total;
                }
            }

            public static class ImagesBean {
                private int height;
                private String url;
                private int width;

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }
            }
        }
    }
}
