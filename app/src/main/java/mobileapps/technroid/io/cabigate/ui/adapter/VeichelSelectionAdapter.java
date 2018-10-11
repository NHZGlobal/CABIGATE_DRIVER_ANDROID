package mobileapps.technroid.io.cabigate.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mobileapps.technroid.io.cabigate.R;


public class VeichelSelectionAdapter extends RecyclerView.Adapter<VeichelSelectionAdapter.CustomViewHolder> {

    private Activity mContext;
    private CustomViewHolder customViewHolder;
    private int i;

    private OnItemClickListener onItemClickListener;
    private List<mobileapps.technroid.io.cabigate.models.List> lists;



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    public VeichelSelectionAdapter(Activity context, List<mobileapps.technroid.io.cabigate.models.List> lists) {
        this.lists = lists;
        this.mContext = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        final View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_veichle, null);

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
        mobileapps.technroid.io.cabigate.models.List list = lists.get(i);
        customViewHolder.tvTitle.setText(list.name);
        customViewHolder.tvCarNumber.setText(list.number);
       // customViewHolder.tvCarAdress.setText("672,k johar town,loahore");

        customViewHolder.ivPhoto.setImageResource(R.drawable.free_taxi_ic);
        customViewHolder.tvCarAdress.setVisibility(View.GONE);
        if(list.status.equals("booked")){
            customViewHolder.ivPhoto.setImageResource(R.drawable.booked_taxi_ic);
            customViewHolder.tvCarAdress.setText(list.driver);
            customViewHolder.tvCarAdress.setVisibility(View.VISIBLE);
        }


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

        @Bind(R.id.ivCarStatus)
        ImageView ivPhoto;
        @Bind(R.id.tvCarName)
        TextView tvTitle;
        @Bind(R.id.tvCarNumber)
        TextView tvCarNumber;
        @Bind(R.id.tvCarAdress)
        TextView tvCarAdress;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
