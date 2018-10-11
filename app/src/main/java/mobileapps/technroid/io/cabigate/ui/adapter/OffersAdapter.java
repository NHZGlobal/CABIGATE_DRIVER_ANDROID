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
import mobileapps.technroid.io.cabigate.models.Job;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.CustomViewHolder> {

    private Activity mContext;
    private CustomViewHolder customViewHolder;
    private int i;

    private OnItemClickListener onItemClickListener;
    private List<Job> lists;



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    public OffersAdapter(Activity context, List<Job> lists) {
        this.lists = lists;
        this.mContext = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        final View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_offer, null);

        final CustomViewHolder viewHolder = new CustomViewHolder(view1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onItemClickListener.onItemClick(view1, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        //Setting text view title
        Job job = lists.get(i);
       //CommonMethods.setTextSpanColor(customViewHolder.tvWhen, "When:" + job.getWhen(), job.getWhen(), mContext.getResources().getColor(R.color.colorAccent));
        CommonMethods.setTextSpanColor(customViewHolder.tvPickUp, "Pick up:" + job.getPickup(), job.getPickup(), mContext.getResources().getColor(R.color.colorAccent));
        CommonMethods.setTextSpanColor(customViewHolder.tvDropOff, "Drop off:" + job.getDropoff(), job.getDropoff(), mContext.getResources().getColor(R.color.colorAccent));
        customViewHolder.tvTime.setText(job.getDuration());
        customViewHolder.tvFair.setText(job.getFare() + " GBP");
        customViewHolder.tvWhen.setText(job.getPickup_date());
        customViewHolder.tvNote.setText("Note:"+job.getNotes());
        customViewHolder.tvCarType.setText("VehicleType:"+job.getVehicle_type());

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

        @Bind(R.id.tvWhen)
        TextView tvWhen;
        @Bind(R.id.tvDropOff)
        TextView tvDropOff;
        @Bind(R.id.tvPickUp)
        TextView tvPickUp;
        @Bind(R.id.tvTime)
        TextView tvTime;
        @Bind(R.id.tvFair)
        TextView tvFair;
        @Bind(R.id.tvCarType)
        TextView tvCarType;
        @Bind(R.id.tvNote)
        TextView tvNote;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
