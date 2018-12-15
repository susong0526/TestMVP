package com.example.susong.testmvp.business.main.view;


import com.example.susong.testmvp.entity.po.User;
import com.example.susong.testmvp.framework.BaseView;

import java.util.List;

public interface MainBaseView extends BaseView {
    void displayData(List<User> users);
}
