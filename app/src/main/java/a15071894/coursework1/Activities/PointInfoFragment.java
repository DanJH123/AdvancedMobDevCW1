package a15071894.coursework1.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import a15071894.coursework1.Points.APointInfo;
import a15081794.coursework1.R;

/*
* This class is for customising a dialog fragment and allowing the program to feed information into
* it when opened.
* */
public class PointInfoFragment extends DialogFragment {

    private static ArrayList<APointInfo> pointInfoList;
    private ListView pointInfoLV;

    public static PointInfoFragment newInstance(ArrayList<APointInfo> departures){
        PointInfoFragment pointInfoFragment = new PointInfoFragment();
        pointInfoList = departures;
        Bundle args = new Bundle();
        pointInfoFragment.setArguments(args);
        return pointInfoFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Adds information to the dialog fragment in the form of a list view.
        View v = getActivity().getLayoutInflater().inflate(R.layout.point_info_layout, null);
        pointInfoLV = (ListView)v.findViewById(R.id.pointInfo_list);
        ArrayAdapter<APointInfo> pointInfoAdapter = new ArrayAdapter<>
                            (this.getContext(), android.R.layout.simple_list_item_1, pointInfoList);
        pointInfoLV.setAdapter(pointInfoAdapter);

        pointInfoAdapter.notifyDataSetChanged();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Point Information").setView(v);
        return builder.create();
    }
}
