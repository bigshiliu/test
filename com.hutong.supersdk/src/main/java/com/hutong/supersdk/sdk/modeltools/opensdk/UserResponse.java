package com.hutong.supersdk.sdk.modeltools.opensdk;

public class UserResponse {

    private String status;
    private User user;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        private UserInfo user_info;
        private OpenInfo open_info;

        public UserInfo getUser_info() {
            return user_info;
        }

        public void setUser_info(UserInfo user_info) {
            this.user_info = user_info;
        }

        public OpenInfo getOpen_info() {
            return open_info;
        }

        public void setOpen_info(OpenInfo open_info) {
            this.open_info = open_info;
        }
    }

    public static class UserInfo {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class OpenInfo {
        private String open_id;
        private String token;
        private long validate_time;

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getValidate_time() {
            return validate_time;
        }

        public void setValidate_time(long validate_time) {
            this.validate_time = validate_time;
        }
    }
}
