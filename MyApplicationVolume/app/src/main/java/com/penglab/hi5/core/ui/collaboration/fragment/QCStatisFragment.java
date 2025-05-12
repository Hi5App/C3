package com.penglab.hi5.core.ui.collaboration.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.penglab.hi5.R;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.core.ui.collaboration.adapter.QCMarkerTableAdapter;

import java.util.HashMap;
import java.util.List;

public class QCStatisFragment extends Fragment {
    private final List<ImageMarker> unCheckedMarkerList;
    private final int removedOverlapSegNum;
    private final int removedErrSegNum;

    private HashMap<String, Integer> type2SizeMap;
    private TextView totalNumberTextView;
    private TextView multifurcationNumberTextView;
    private TextView loopNumberTextView;
    private TextView missingNumberTextView;
    private TextView crossingDirectionErrNumberTextView;
    private TextView colorMutationNumberTextView;
    private TextView isolatedBranchNumberTextView;
    private TextView angleErrNumberTextView;
    private TextView overlapBranchNumberTextView;
    private TextView errSegNumberTextView;

    private TextView approachingBifNumberTextView;


    public QCStatisFragment(List<ImageMarker> unCheckedMarkerList, int removedOverlapSegNum, int removedErrSegNum) {
        this.unCheckedMarkerList = unCheckedMarkerList;
        this.removedErrSegNum = removedErrSegNum;
        this.removedOverlapSegNum = removedOverlapSegNum;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qc_statis, container, false);
        TextView title = view.findViewById(R.id.titleTextView);
        title.setText("QC Statisticians");

        type2SizeMap = new HashMap<>();
        for (ImageMarker marker : unCheckedMarkerList) {
            Integer oldNumber = type2SizeMap.getOrDefault(marker.comment, 0);
            if (oldNumber != null) {
                type2SizeMap.put(marker.comment, oldNumber + 1);
            }
        }

        totalNumberTextView = view.findViewById(R.id.total_number_value);
        multifurcationNumberTextView = view.findViewById(R.id.multifurcation_value);
        loopNumberTextView = view.findViewById(R.id.loop_value);
//        approachingBifNumberTextView = view.findViewById(R.id.Approaching_bifurcation_value);
        missingNumberTextView = view.findViewById(R.id.missing_value);
        crossingDirectionErrNumberTextView = view.findViewById(R.id.crossing_direction_error_value);
        colorMutationNumberTextView = view.findViewById(R.id.color_mutation_value);

        isolatedBranchNumberTextView = view.findViewById(R.id.isolated_branch_value);
        angleErrNumberTextView = view.findViewById(R.id.angle_error_value);

        totalNumberTextView.setText(unCheckedMarkerList.size() + "");
        Integer multifurcationNum = type2SizeMap.getOrDefault("Multifurcation", 0);
        if (multifurcationNum != null) {
            multifurcationNumberTextView.setText(multifurcationNum + "");
        } else {
            multifurcationNumberTextView.setText(0 + "");
        }
        Integer loopNum = type2SizeMap.getOrDefault("Loop", 0);
        if (loopNum != null) {
            loopNumberTextView.setText(loopNum + "");
        } else {
            loopNumberTextView.setText(0 + "");
        }
//        Integer approachingBifNum = type2SizeMap.getOrDefault("Approaching bifurcation", 0);
//        if (approachingBifNum != null) {
//            approachingBifNumberTextView.setText(approachingBifNum + "");
//        } else {
//            approachingBifNumberTextView.setText(0 + "");
//        }
        Integer missingNum = type2SizeMap.getOrDefault("Missing", 0);
        if (missingNum != null) {
            missingNumberTextView.setText(missingNum + "");
        } else {
            missingNumberTextView.setText(0 + "");
        }
        Integer crossingDirectionErrNum = type2SizeMap.getOrDefault("Crossing error", 0);
        if (crossingDirectionErrNum != null) {
            crossingDirectionErrNumberTextView.setText(crossingDirectionErrNum + "");
        } else {
            crossingDirectionErrNumberTextView.setText(0 + "");
        }
        Integer colorMutationNum = type2SizeMap.getOrDefault("Color mutation", 0);
        if (colorMutationNum != null) {
            colorMutationNumberTextView.setText(colorMutationNum + "");
        } else {
            colorMutationNumberTextView.setText(0 + "");
        }
        Integer isolatedBranchNum = type2SizeMap.getOrDefault("Dissociative seg", 0);
        if (isolatedBranchNum != null) {
            isolatedBranchNumberTextView.setText(isolatedBranchNum + "");
        } else {
            isolatedBranchNumberTextView.setText(0 + "");
        }
        Integer angleErrNum = type2SizeMap.getOrDefault("Angle error", 0);
        if (angleErrNum != null) {
            angleErrNumberTextView.setText(angleErrNum + "");
        } else {
            angleErrNumberTextView.setText(0 + "");
        }

        overlapBranchNumberTextView = view.findViewById(R.id.overlap_branch_value);
        overlapBranchNumberTextView.setText(removedOverlapSegNum + "");
        errSegNumberTextView = view.findViewById(R.id.error_seg_value);
        errSegNumberTextView.setText(removedErrSegNum + "");

        return view;
    }

    // 更新数据的方法
    public void updateData(List<ImageMarker> unCheckedMarkerList, int removedOverlapSegNum, int removedErrSegNum) {
        type2SizeMap.clear();
        for (ImageMarker marker : unCheckedMarkerList) {
            Integer oldNumber = type2SizeMap.getOrDefault(marker.comment, 0);
            if (oldNumber != null) {
                type2SizeMap.put(marker.comment, oldNumber + 1);
            }
        }

        totalNumberTextView.setText(unCheckedMarkerList.size());
        Integer multifurcationNum = type2SizeMap.getOrDefault("Multifurcation", 0);
        if (multifurcationNum != null) {
            multifurcationNumberTextView.setText(multifurcationNum);
        } else {
            multifurcationNumberTextView.setText(0);
        }
        Integer loopNum = type2SizeMap.getOrDefault("Loop", 0);
        if (loopNum != null) {
            loopNumberTextView.setText(loopNum);
        } else {
            loopNumberTextView.setText(0);
        }
        Integer missingNum = type2SizeMap.getOrDefault("Missing", 0);
        if (missingNum != null) {
            missingNumberTextView.setText(missingNum);
        } else {
            missingNumberTextView.setText(0);
        }
        Integer crossingDirectionErrNum = type2SizeMap.getOrDefault("Crossing error", 0);
        if (crossingDirectionErrNum != null) {
            crossingDirectionErrNumberTextView.setText(crossingDirectionErrNum);
        } else {
            crossingDirectionErrNumberTextView.setText(0);
        }
        Integer colorMutationNum = type2SizeMap.getOrDefault("Color mutation", 0);
        if (colorMutationNum != null) {
            colorMutationNumberTextView.setText(colorMutationNum);
        } else {
            colorMutationNumberTextView.setText(0);
        }
        Integer isolatedBranchNum = type2SizeMap.getOrDefault("Dissociative seg", 0);
        if (isolatedBranchNum != null) {
            isolatedBranchNumberTextView.setText(isolatedBranchNum);
        } else {
            isolatedBranchNumberTextView.setText(0);
        }
        Integer angleErrNum = type2SizeMap.getOrDefault("Angle error", 0);
        if (angleErrNum != null) {
            angleErrNumberTextView.setText(angleErrNum);
        } else {
            angleErrNumberTextView.setText(0);
        }

        overlapBranchNumberTextView.setText(removedOverlapSegNum);
        errSegNumberTextView.setText(removedErrSegNum);
    }

}

