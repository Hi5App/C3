package com.penglab.hi5.core.game.leaderBoard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.penglab.hi5.R;



/**
 * A fragment representing a list of Items.
 */
public class TodayFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private LeaderBoardAdapter adapter;

    private LeaderBoardViewModel leaderBoardViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodayFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TodayFragment newInstance(int columnCount) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = view.findViewById(R.id.today_recyclerview);
//            leaderBoardViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelFactory()).get(LeaderBoardViewModel.class);
//            adapter = new LeaderBoardAdapter(leaderBoardViewModel.getLeaderBoardItemList());
//            recyclerView.setAdapter(adapter);
//            recyclerView.setAdapter(new MyItemRecyclerViewAdapter2(PlaceholderContent.ITEMS));
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

}
