package com.example.viewpagerforgridview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CookieListFragment extends Activity implements OnItemClickListener {
	private ViewPager viewpager;
	private TextView num1, num2;
	private List<View> viewList = new ArrayList<View>();
	private List<Integer> list = new ArrayList<Integer>();
	/**
	 * 一页的商品最大个数
	 */
	private int one_page_num;
	/**
	 * 页数
	 */
	private int page_num = 0;
	/**
	 * 最后一页的商品个数
	 */
	private int last_page_num = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_cookie_list);

		init();
	}

	public void init() {
		initView();
		initData();
	}

	public void initView() {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		viewpager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				num1.setText(arg0 + 1 + "");
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		num1 = (TextView) findViewById(R.id.num1);
		num2 = (TextView) findViewById(R.id.num2);
	}

	public void initData() {
		list.clear();
		for (int i = 0; i < 100; i++) {
			list.add(i);
		}
		
		one_page_num=Integer.parseInt(getResources().getString(R.string.gridview_row))*Integer.parseInt(getResources().getString(R.string.gridview_column));

		// 分页
		page_num = (int) (list.size() / one_page_num);
		if (list.size() % one_page_num != 0) {
			page_num = page_num + 1;
		}
		// 最后一页的数据条数
		if (list.size() % one_page_num != 0) {
			last_page_num = list.size() % one_page_num;
		} else {
			last_page_num = one_page_num;
		}
		viewList.clear();
		// 为每页填充数据
		for (int i = 0; i < page_num; i++) {
			View view;
			if (i == page_num - 1) {
				view = LayoutInflater.from(this).inflate(
						R.layout.viewpager_content, null);
				GridView gridview = (GridView) view.findViewById(R.id.gridview);
				gridview.setAdapter(new MGridViewAdapter(this, i, last_page_num));
				gridview.setOnItemClickListener(CookieListFragment.this);
			} else {
				view = LayoutInflater.from(this).inflate(
						R.layout.viewpager_content, null);
				GridView gridview = (GridView) view.findViewById(R.id.gridview);
				gridview.setAdapter(new MGridViewAdapter(this, i, one_page_num));
				gridview.setOnItemClickListener(CookieListFragment.this);
			}
			viewList.add(view);
		}
		if (viewList.size() == 0) {
			viewList.add(LayoutInflater.from(this).inflate(
					R.layout.viewpager_content, null));
		}
		viewpager.setAdapter(pagerAdapter);

		num1.setText("1");
		if (page_num == 0) {
			num2.setText("1");
		} else {
			num2.setText(page_num + "");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	PagerAdapter pagerAdapter = new PagerAdapter() {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			return viewList.get(position);
		}
	};

	class MGridViewAdapter extends BaseAdapter {
		Context context;
		int index, num;

		public MGridViewAdapter(Context context, int index, int num) {
			this.context = context;
			this.index = index;
			this.num = num;
		}

		@Override
		public int getCount() {
			return num;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position + index * one_page_num;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			position = position + index * one_page_num;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.goods_item, null);
				// 动态设置这个高度即可。保证刚好填满viewpager界面
				convertView.setLayoutParams(new GridView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, (viewpager.getHeight()
								- dip2px(context,(getResources().getDimension(R.dimen.gridview_gap)/2)*(Integer.parseInt(getResources().getString(R.string.gridview_row))-1))) 
								/ Integer.parseInt(getResources().getString(R.string.gridview_row))));
				
				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.image);
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				
				//这里也必须添加这个，否则第一个item会显示不正常
				convertView.setLayoutParams(new GridView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, (viewpager.getHeight()
								- dip2px(context,(getResources().getDimension(R.dimen.gridview_gap)/2)*(Integer.parseInt(getResources().getString(R.string.gridview_row))-1))) 
								/ Integer.parseInt(getResources().getString(R.string.gridview_row))));
			}

			viewHolder.image.setImageResource(R.drawable.img);
			viewHolder.name.setText("第"+(position+1)+"个");
			return convertView;
		}

		class ViewHolder {
			ImageView image;
			TextView name;
		}
	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
}
