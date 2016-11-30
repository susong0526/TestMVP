package com.example.susong.testmvp.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jlhm.personal.R;
import com.jlhm.personal.crosslineshopping.fragment.FragmentMakeOrder;
import com.jlhm.personal.crosslineshopping.fragment.FragmentMessageNew;
import com.jlhm.personal.crosslineshopping.view.OrdersRecyclerView;
import com.jlhm.personal.model.Order;
import com.jlhm.personal.model.RecieverAddress;
import com.jlhm.personal.model.eventbus.ActivityFinishEvent;
import com.jlhm.personal.ui.ActivityBase;
import com.jlhm.personal.ui.ActivityHaiTao;
import com.jlhm.personal.ui.FragmentEditShippingAddress;
import com.jlhm.personal.ui.FragmentMessage;
import com.jlhm.personal.ui.FragmentMyOrder;
import com.jlhm.personal.ui.FragmentPaymentMode;
import com.jlhm.personal.ui.FragmentShippingAddress;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 兼容旧版本Activity,Fragment
 *
 * @author Scott Smith  @Date 2016年06月16/6/12日 12:12
 */
public class ActivityCompatBridge extends ActivityBase {

    // 来自页面
    public static final String KEY_FROM_PAGE = "from_page";
    // 额外数据
    public static final String KEY_EXTRA_DATA = "extra_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setId(R.id.container);
        addContentView(frameLayout);
        String page = getIntent().getStringExtra(KEY_FROM_PAGE);
        // 来自新版下单页面
        if (FragmentMakeOrder.class.getName().equals(page)) {
            Intent intent = getIntent();
            int oper = getIntent().getIntExtra(FragmentMakeOrder.KEY_OPERATE, 0);
            switch (oper) {
                // 创建订单
                case FragmentMakeOrder.OPER_MAKE_ORDER: {
                    long orderId = getIntent().getLongExtra(FragmentMakeOrder.KEY_ORDER_ID, -1);
                    if (orderId > 0) {
                        FragmentPaymentMode.mOrderId = orderId;
                        if (ActivityHaiTao.class.getName().equals(getIntent().getStringExtra(FragmentMakeOrder.KEY_OWNER_ACTIVITY))) {
                            FragmentPaymentMode.mSkipPage = 3;
                        } else {
                            FragmentPaymentMode.mSkipPage = 2;
                        }
                        FragmentPaymentMode fragmentPaymentMode = new FragmentPaymentMode();
                        switchFragment2(fragmentPaymentMode, R.id.container, false, false);
                    }
                    break;
                }
                // 选择收货地址
                case FragmentMakeOrder.OPER_SELECT_ADDR: {
                    long addressId = intent.getLongExtra(FragmentMakeOrder.KEY_RECV_ADDR_ID, -1);
                    FragmentShippingAddress shippingAddress = FragmentShippingAddress.newInstance(addressId, 1);
                    switchFragment2(shippingAddress, R.id.container, false, false);
                    break;
                }
                // 修改收货地址
                case FragmentMakeOrder.OPER_EDIT_ADDR: {
                    RecieverAddress mCurrRecvAddr = (RecieverAddress) intent.getSerializableExtra(FragmentMakeOrder.KEY_RECV_ADDR);
                    FragmentEditShippingAddress fragmentEditShippingAddress = FragmentEditShippingAddress.newInstance(mCurrRecvAddr, 1);
                    switchFragment2(fragmentEditShippingAddress, R.id.container, false, false);
                    break;
                }
            }
        }

        // 来自首页点击消息页面跳转
        if (FragmentMessage.class.getName().equals(page)) {
            FragmentMessageNew messageNew = new FragmentMessageNew();
            switchFragment2(messageNew, R.id.container, false, false);
        }

        // 来自海淘订单列表页面
        if (OrdersRecyclerView.class.getName().equals(page)) {
            int operate = getIntent().getIntExtra(OrdersRecyclerView.KEY_OPERATE, -1);
            switch (operate) {
                case OrdersRecyclerView.OPERATE_TO_DETAIL: {
                    // 跳转至订单详情页面
                    Order order = (Order) getIntent().getSerializableExtra(OrdersRecyclerView.KEY_ORDER_DETAIL);
                    FragmentMyOrder.mOrderType = FragmentMyOrder.TYPE_PERSON_ORDER;
                    FragmentMyOrder.mOrder = order;
                    FragmentMyOrder fragmentMyOrder = new FragmentMyOrder();
                    switchFragment2(fragmentMyOrder, R.id.container, false, false);
                    break;
                }
                case OrdersRecyclerView.OPERATE_TO_PAY: {
                    long orderId = getIntent().getLongExtra(OrdersRecyclerView.KEY_ORDER_ID, -1);
                    if (orderId > 0) {
                        FragmentPaymentMode.mOrderId = orderId;
                        // -_-||
                        FragmentPaymentMode.mSkipPage = 2;
                        FragmentPaymentMode fragmentPaymentMode = new FragmentPaymentMode();
                        switchFragment2(fragmentPaymentMode, R.id.container, false, false);
                    }
                    break;
                }
            }

        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onEvent(ActivityFinishEvent e) {
        if (e.getAction() == ActivityFinishEvent.FinishAction.FINISH_ACTIVITY_COMPAT_BRIDGE) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
