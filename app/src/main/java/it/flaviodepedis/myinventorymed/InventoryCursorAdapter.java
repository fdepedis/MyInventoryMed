package it.flaviodepedis.myinventorymed;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.flaviodepedis.myinventorymed.data.InventoryMedContract.InventoryMedEntry;

/**
 * Created by flavio.depedis on 01/10/2017.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    private static Context mContext;

    private ViewHolder holder;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is not null, then inflate a new list item layout.
        LayoutInflater inflater = LayoutInflater.from(context);
        View listItemView = inflater.inflate(R.layout.list_item,parent,false);
        holder = new ViewHolder(listItemView);
        listItemView.setTag(holder);
        return listItemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find the columns of pet attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(InventoryMedEntry._ID);
        int medNameColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_NAME);
        int medTypeColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_TYPE);
        int medQuantityColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_QUANTITY);
        int medExpDateColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_EXP_DATE);
        int medPriceColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_PRICE);
        int medPriceDiscountColumnIndex = cursor.getColumnIndex(InventoryMedEntry.COLUMN_MED_PRICE_DISCOUNT);

        // Read the medicine attributes from the Cursor for the current med
        String medName = cursor.getString(medNameColumnIndex);
        String medType = cursor.getString(medTypeColumnIndex);
        int medQuantity = cursor.getInt(medQuantityColumnIndex);
        String medExpDate = cursor.getString(medExpDateColumnIndex);
        Double medPrice = cursor.getDouble(medPriceColumnIndex);
        Double medPriceDiscount = cursor.getDouble(medPriceDiscountColumnIndex);

        // Update the TextViews with the attributes for the current med
        holder.tvMedName.setText(medName);
        holder.tvMedType.setText(medType);
        holder.tvMedQuantity.setText((String.valueOf(medQuantity)));
        holder.tvMedPrice.setText(context.getString(R.string.label_price, medPrice));
        //verify if discount exist
        if(medPriceDiscount > 0){
            holder.tvMedPriceDiscount.setText(context.getString(R.string.label_price_discount, medPriceDiscount));
        } else {
            holder.tvMedPriceDiscount.setText(context.getString(R.string.empty_discount_price, medPriceDiscount));
        }
        holder.tvMedExpDate.setText(context.getString(R.string.label_exp_date, medExpDate));
    }

    static class ViewHolder {
        @BindView(R.id.tv_med_name) TextView tvMedName;
        @BindView(R.id.tv_med_type) TextView tvMedType;
        @BindView(R.id.tv_med_price) TextView tvMedPrice;
        @BindView(R.id.tv_med_price_discount) TextView tvMedPriceDiscount;
        @BindView(R.id.tv_med_quantity) TextView tvMedQuantity;
        @BindView(R.id.tv_med_exp_date) TextView tvMedExpDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
