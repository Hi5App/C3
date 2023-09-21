package com.penglab.hi5.core.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.penglab.hi5.core.game.leaderBoard.LeaderBoardItemModel;
import com.penglab.hi5.core.game.leaderBoard.LeaderBoardViewModel;
import com.penglab.hi5.core.game.quest.QuestViewModel;
import com.penglab.hi5.core.ui.BoutonDetection.BoutonDetectionViewModel;
import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionViewModel;
import com.penglab.hi5.core.ui.annotation.AnnotationViewModel;
import com.penglab.hi5.core.ui.check.CheckArborInfoState;
import com.penglab.hi5.core.ui.check.CheckViewModel;
import com.penglab.hi5.core.ui.collaboration.CollaborationViewModel;
import com.penglab.hi5.core.ui.home.screens.HomeViewModel;
import com.penglab.hi5.core.ui.login.LoginViewModel;
import com.penglab.hi5.core.ui.marker.MarkerFactoryViewModel;
import com.penglab.hi5.core.ui.password.FindPasswordViewModel;
import com.penglab.hi5.core.ui.register.RegisterViewModel;
import com.penglab.hi5.core.ui.splash.SplashScreenViewModel;
import com.penglab.hi5.core.ui.userProfile.MyViewModel;
import com.penglab.hi5.data.AnnotationDataSource;
import com.penglab.hi5.data.BoutonDetectionDataSource;
import com.penglab.hi5.data.CheckArborDataSource;
import com.penglab.hi5.data.CheckDataSource;
import com.penglab.hi5.data.CollorationDataSource;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.MarkerFactoryDataSource;
import com.penglab.hi5.data.QualityInspectionDataSource;
import com.penglab.hi5.data.ResourceDataSource;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.UserPerformanceDataSource;
import com.penglab.hi5.data.model.user.LoggedInUser;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            // used in LoginActivity
            return (T) new LoginViewModel(UserInfoRepository.getInstance(), new UserDataSource());
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            // used in RegisterActivity
            return (T) new RegisterViewModel(new UserDataSource());
        } else if (modelClass.isAssignableFrom(FindPasswordViewModel.class)) {
            // used in FindPasswordActivity
            return (T) new FindPasswordViewModel(new UserDataSource());
        } else if (modelClass.isAssignableFrom(SplashScreenViewModel.class)) {
            // used in SplashScreenActivity
            return (T) new SplashScreenViewModel(UserInfoRepository.getInstance(), new ResourceDataSource(), new UserDataSource());
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            // used in HomeActivity
            return (T) new HomeViewModel(UserInfoRepository.getInstance(), new UserDataSource(), new ResourceDataSource());
        } else if (modelClass.isAssignableFrom(CheckViewModel.class)) {
            // used in HomeActivity
            return (T) new CheckViewModel(new ImageDataSource(), new AnnotationDataSource(), new CheckDataSource(), ImageInfoRepository.getInstance(), new CheckArborDataSource(), CheckArborInfoState.getInstance());
        } else if (modelClass.isAssignableFrom(AnnotationViewModel.class)) {
            // used in AnnotationActivity
            return (T) new AnnotationViewModel(ImageInfoRepository.getInstance(), UserInfoRepository.getInstance(), new UserDataSource(), new ImageDataSource());
        } else if (modelClass.isAssignableFrom(QuestViewModel.class)) {
            return (T) new QuestViewModel();
        } else if (modelClass.isAssignableFrom(LeaderBoardViewModel.class)) {
            return (T) new LeaderBoardViewModel(LeaderBoardItemModel.getInstance());
        } else if (modelClass.isAssignableFrom(MarkerFactoryViewModel.class)) {
            return (T) new MarkerFactoryViewModel(UserInfoRepository.getInstance(), ImageInfoRepository.getInstance(), new MarkerFactoryDataSource(), new ImageDataSource());
        } else if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel(UserInfoRepository.getInstance(), new UserDataSource(), new UserPerformanceDataSource());
        } else if(modelClass.isAssignableFrom(QualityInspectionViewModel.class)) {
            return (T) new QualityInspectionViewModel(UserInfoRepository.getInstance(), ImageInfoRepository.getInstance(), new QualityInspectionDataSource(), new ImageDataSource());
        } else if(modelClass.isAssignableFrom(CollaborationViewModel.class)){
            return(T) new CollaborationViewModel(UserInfoRepository.getInstance(), ImageInfoRepository.getInstance(),new ImageDataSource(),new CollorationDataSource());

        } else if(modelClass.isAssignableFrom(BoutonDetectionViewModel.class)){
            return(T) new BoutonDetectionViewModel(UserInfoRepository.getInstance(), ImageInfoRepository.getInstance(),new BoutonDetectionDataSource(),new ImageDataSource());

        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}