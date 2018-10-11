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
import mobileapps.technroid.io.cabigate.models.Driver;


public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.CustomViewHolder> {

    private Activity mContext;
    private CustomViewHolder customViewHolder;
    private int i;

    private OnItemClickListener onItemClickListener;
    private List<Driver> lists;



    public interface OnItemClickListener {
        public void onDriverItemClick(View view, int position);
    }


    public DriverAdapter(Activity context, List<Driver> lists) {
        this.lists = lists;
        this.mContext = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        final View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_driver, null);

        final CustomViewHolder viewHolder = new CustomViewHolder(view1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onItemClickListener.onDriverItemClick(view1, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        //Setting text view title
        Driver driver = lists.get(i);

        customViewHolder.tvName.setText(driver.getDriver_name());
        customViewHolder.tvPhone.setText(driver.getPhone());
        customViewHolder.tvTaxiColor.setText(driver.getTaxi_color());
        customViewHolder.tvDistance.setText(driver.getDistance());




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

        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.tvPhone)
        TextView tvPhone;
        @Bind(R.id.tvDistance)
        TextView tvDistance;
        @Bind(R.id.tvTaxiColor)
        TextView tvTaxiColor;



        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
