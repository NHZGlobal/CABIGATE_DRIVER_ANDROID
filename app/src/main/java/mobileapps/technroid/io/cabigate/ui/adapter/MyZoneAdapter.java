package mobileapps.technroid.io.cabigate.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.helper.CommonMethods;
import mobileapps.technroid.io.cabigate.models.MyZones;


public class MyZoneAdapter extends RecyclerView.Adapter<MyZoneAdapter.CustomViewHolder> {

    private Activity mContext;
    private CustomViewHolder customViewHolder;
    private int i;

    private OnItemClickListener onItemClickListener;
    private List<MyZones> lists;



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    public MyZoneAdapter(Activity context, List<MyZones> lists) {
        this.lists = lists;
        this.mContext = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        final View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_queue_zones, null);

        final CustomViewHolder viewHolder = new CustomViewHolder(view1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          //     onItemClickListener.onItemClick(view1, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        //Setting text view title
        MyZones myZones = lists.get(i);

        customViewHolder.tvTitle.setText(myZones.getZoneid()+":"+myZones.getName());

        CommonMethods.setTextSpanColor(customViewHolder.tvTitle,myZones.getZoneid()+":"+myZones.getName(),myZones.getZoneid(),
                mContext.getResources().getColor(R.color.colorAccent));
        customViewHolder.tvStatus.setText("Available:" +myZones.getFree()+"/"+"Busy:" +myZones.getBusy());




    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    @Override
    public int getItemCount() {
        //return  8;
        return (null != lists ? lists.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvTitle)
        TextView tvTitle;
        @Bind(R.id.tvStatus)
        TextView tvStatus;



        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
