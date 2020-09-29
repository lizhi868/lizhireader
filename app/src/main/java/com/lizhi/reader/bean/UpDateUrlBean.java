package com.lizhi.reader.bean;

import java.util.List;

public class UpDateUrlBean {

    /**
     * sharegroups : [{"num":"1234567","title":"1群","type":"qq"}]
     * shareaddr : {"addr":"https://www.lanzous.com/lizhireader"}
     */

    private ShareaddrBean shareaddr;
    private List<SharegroupsBean> sharegroups;

    public ShareaddrBean getShareaddr() {
        return shareaddr;
    }

    public void setShareaddr(ShareaddrBean shareaddr) {
        this.shareaddr = shareaddr;
    }

    public List<SharegroupsBean> getSharegroups() {
        return sharegroups;
    }

    public void setSharegroups(List<SharegroupsBean> sharegroups) {
        this.sharegroups = sharegroups;
    }

    public static class ShareaddrBean {
        /**
         * addr : https://www.lanzous.com/lizhireader
         */

        private String addr;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }
    }

    public static class SharegroupsBean {
        /**
         * num : 1234567
         * title : 1群
         * type : qq
         */

        private String num;
        private String title;
        private String type;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
