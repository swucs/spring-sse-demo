package com.hannah.springssedemo.threadlocal;

import com.hannah.springssedemo.entity.User;
import lombok.Data;

@Data
public class CurrentUser {
    private String userId;
    private String userName;

    public CurrentUser(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
    }
}
