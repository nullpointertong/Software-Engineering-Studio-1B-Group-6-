package yourteamnumber.seshealthpatient.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

import yourteamnumber.seshealthpatient.Model.DataPacket.CustomComponents.TextInputComponent;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.DataPacket;
import yourteamnumber.seshealthpatient.Model.DataPacket.Models.TextData;
import yourteamnumber.seshealthpatient.R;

public class DataPacketFragment extends Fragment {


    private RelativeLayout relativeLayout;
    private View lastView;
    private TextView txtAddDataHint;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    private ArrayList<CustomComponents> activeDataComponents;
    private ArrayList<View> activeViews;
    private DataPacket dataPacket;

    public DataPacketFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_packet, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activeDataComponents = new ArrayList<>();
        activeViews = new ArrayList<>();
        dataPacket = new DataPacket();

        txtAddDataHint = getView().findViewById(R.id.txtAddData);
        relativeLayout = getView().findViewById(R.id.frameLayout);
        rfaLayout = getView().findViewById(R.id.activity_main_rfal);
        rfaBtn = getView().findViewById(R.id.activity_main_rfab);

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this.getContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(new RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener() {
            @Override
            public void onRFACItemLabelClick(int position, RFACLabelItem item) {
                HandleItemClick((Integer) item.getWrapper());
            }

            @Override
            public void onRFACItemIconClick(int position, RFACLabelItem item) {
                HandleItemClick((Integer) item.getWrapper());
            }
        });
        List<RFACLabelItem> items = new ArrayList<>();

        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.data_packet_send))
                .setIconNormalColor(0xff00e676)
                .setIconPressedColor(0xff00c853)
                .setDrawable(getResources().getDrawable(R.mipmap.ic_send))
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.text_input_label))
                .setIconNormalColor(0xffd84315)
                .setIconPressedColor(0xffbf360c)
                .setDrawable(getResources().getDrawable(R.drawable.ic_plus))
                .setWrapper(1)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(2)
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(2)
        ;
        rfabHelper = new RapidFloatingActionHelper(
                this.getContext(),
                rfaLayout,
                rfaBtn,
                rfaContent
        );

        rfabHelper.build();
    }

    private void AddTextInput() {
        if (!ComponentExists(CustomComponents.TEXT_INPUT))
        {
            TextInputComponent textInputComponent = new TextInputComponent(getContext());
            textInputComponent.setId(R.id.textInput);
            relativeLayout.addView(textInputComponent);

            if (lastView != null)
            {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, lastView.getId());
                textInputComponent.setLayoutParams(layoutParams);
            }

            lastView = textInputComponent;
            activeDataComponents.add(CustomComponents.TEXT_INPUT);
            activeViews.add(textInputComponent);
        }
        else
        {
            Snackbar.make(getView(), "You have already added a Description to the data packet.", Snackbar.LENGTH_LONG).show();
        }

    }

    private void Send()
    {
        for (int i = 0; i < activeViews.size(); i++)
        {
            if (activeViews.get(i) != null)
            {
                CustomComponents currentComponent = activeDataComponents.get(i);
                AddForComponent(activeViews.get(i), currentComponent);
            }
        }

        dataPacket.Send(getContext());
    }

    private boolean ComponentExists(CustomComponents customComponent)
    {
        return activeDataComponents.contains(customComponent);
    }

    private void AddForComponent(View activeView, CustomComponents customComponent)
    {
        switch (customComponent)
        {
            case TEXT_INPUT:
                TextData textData = ((TextInputComponent)activeView).getTextData();
                dataPacket.addTextData(textData);
                break;
            default: ShowAlertDialog(customComponent.name() + " is not a valid component."); break;
        }
    }

    private void HandleItemClick(int dataId)
    {
        txtAddDataHint.setVisibility(View.INVISIBLE);

        switch (dataId)
        {
            case 0: Send(); break;
            case 1: AddTextInput(); break;
            default: break;
        }
    }

    private void ShowAlertDialog(String message)
    {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(getContext())
                        .setTitle("Error: ")
                        .setMessage(message)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialogBuilder.show();
    }

    private enum CustomComponents
    {
        TEXT_INPUT
    }
}
