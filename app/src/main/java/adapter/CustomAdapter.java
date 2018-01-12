package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.model.PlaceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agamy on 1/10/2018.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    OnItemClickListenerInterface mListenerInterface;
    List<PlaceModel> myPlaceModels;
    public CustomAdapter(OnItemClickListenerInterface listenerInterface) {
        this.mListenerInterface = listenerInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return myPlaceModels.size();
    }

    public interface OnItemClickListenerInterface{
        void onItemClick(int pos);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_recycle_place);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            tvAddress.setText(myPlaceModels.get(position).getAddress());
            itemView.setTag(myPlaceModels.get(position).getId());
        }

        @Override
        public void onClick(View view) {
            mListenerInterface.onItemClick(getAdapterPosition());
        }
    }

    public void setMyPlaceModels(List<PlaceModel> myPlaceModels) {
        this.myPlaceModels = myPlaceModels;
        notifyDataSetChanged();
    }
}
