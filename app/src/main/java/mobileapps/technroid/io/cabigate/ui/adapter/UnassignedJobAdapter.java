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
import mobileapps.technroid.io.cabigate.models.Job;


public class UnassignedJobAdapter extends RecyclerView.Adapter<UnassignedJobAdapter.CustomViewHolder> {

    private Activity mContext;
    private CustomViewHolder customViewHolder;
    private int i;

    private OnItemClickListener onItemClickListener;
    private List<Job> lists;



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    public UnassignedJobAdapter(Activity context, List<Job> lists) {
        this.lists = lists;
        this.mContext = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        final View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_unassigned_job, null, false);

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
        customViewHolder.tvPickUp.setText( job.getPickup());
        customViewHolder.tvDropOff.setText(job.getDropoff());


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


        @Bind(R.id.tvDropOff)
        TextView tvDropOff;
        @Bind(R.id.tvPickUp)
        TextView tvPickUp;



        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
