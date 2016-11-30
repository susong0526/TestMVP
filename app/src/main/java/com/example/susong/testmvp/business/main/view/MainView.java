package com.example.susong.testmvp.business.main.view;


import com.example.susong.testmvp.entity.dto.User;
import com.example.susong.testmvp.framework.View;

import java.util.List;

public interface MainView extends View {
    void displayData(List<User> users);
}
