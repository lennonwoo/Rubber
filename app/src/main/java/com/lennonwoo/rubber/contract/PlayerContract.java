package com.lennonwoo.rubber.contract;

import com.lennonwoo.rubber.contract.base.BasePresenter;
import com.lennonwoo.rubber.contract.base.BaseView;

public interface PlayerContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

    }

}
