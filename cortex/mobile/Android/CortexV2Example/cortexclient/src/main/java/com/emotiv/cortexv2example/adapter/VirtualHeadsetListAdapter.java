package com.emotiv.cortexv2example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.emotiv.cortexv2example.R;
import com.emotiv.cortexv2example.interfaces.VirtualHeadsetInterface;
import com.emotiv.cortexv2example.objects.VirtualHeadsetObject;

import java.util.List;

public class VirtualHeadsetListAdapter extends ArrayAdapter<VirtualHeadsetObject>  {
    private Context context;
    private List<VirtualHeadsetObject> headsets;
    VirtualHeadsetInterface mVhsInterface = null;

    public VirtualHeadsetListAdapter(Context context, List<VirtualHeadsetObject> headsets) {
        super(context, 0, headsets);
        this.context = context;
        this.headsets = headsets;
    }

    public void setVirtualHeadsetInterface(VirtualHeadsetInterface vhsInterface) {
        mVhsInterface = vhsInterface;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("VirtualHeadsetListAdapter", "getView" + headsets.size());
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.virtual_headset_list_item, parent, false);
        }
        TextView tvHeadsetName = (TextView) convertView.findViewById(R.id.tvHeadsetName);
        tvHeadsetName.setText(headsets.get(position).getType() + "-" + headsets.get(position).getSerial());

        TextView tvHeadsetConnectedBy = (TextView) convertView.findViewById(R.id.tvConnectionType);
        tvHeadsetConnectedBy.setText(headsets.get(position).getConnectionType());

        Button btnPower = (Button) convertView.findViewById(R.id.btnPower);
        btnPower.setText(headsets.get(position).getPowerOn() ? "on" : "off");
        btnPower.setTextColor(headsets.get(position).getPowerOn() ? Color.WHITE : Color.BLACK);
        btnPower.setTag(position);

        btnPower.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int position = (int) v.getTag();
                VirtualHeadsetObject vhs = headsets.get(position);
                String virtualHeadsetId = vhs.getUuid();
                boolean powerOn = vhs.getPowerOn();
                boolean newPowerStatus = ! powerOn;
                if (mVhsInterface != null) {
                    mVhsInterface.onVirtualHeadserPowerChanged(virtualHeadsetId, newPowerStatus);
                }
            }
        });

        Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
        btnDelete.setTag(position);

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int position = (int) v.getTag();
                VirtualHeadsetObject vhs = headsets.get(position);
                String virtualHeadsetId = vhs.getUuid();
                if (mVhsInterface != null) {
                    mVhsInterface.onVirtualHeadserDeleted(virtualHeadsetId);
                }
            }
        });

        return convertView;
    }
}
