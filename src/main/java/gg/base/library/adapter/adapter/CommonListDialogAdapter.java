package gg.base.library.adapter.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import gg.base.library.R;
import gg.base.library.base.MyBaseViewHolder;
import gg.base.library.widget.CommonListDialog;


public class CommonListDialogAdapter extends BaseQuickAdapter<CommonListDialog.Data, MyBaseViewHolder> {


    private final boolean mShowCheckBox;

    public CommonListDialogAdapter(List<CommonListDialog.Data> data, boolean showCheckBox) {
        super(R.layout.layout_select_type_default, data);
        mShowCheckBox = showCheckBox;
    }

    @Override
    protected void convert(MyBaseViewHolder holder, CommonListDialog.Data data) {
        holder.setText(R.id.name, data.getName());
        holder.setImageResource(R.id.img, data.getResId());
        holder.setImageResource(R.id.radio, data.isSelected() ? R.mipmap.frame_a1 : R.mipmap.frame_a2);
        holder.setGone(R.id.radio, mShowCheckBox);
    }
}
