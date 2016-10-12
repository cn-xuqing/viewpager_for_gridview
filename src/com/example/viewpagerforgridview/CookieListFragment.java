package com.example.viewpagerforgridview;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zifeiweb.pzz.R;
import com.zifeiweb.pzz.activity.MenuActivity;
import com.zifeiweb.pzz.bean.GoodsBean;
import com.zifeiweb.pzz.commen.AppConfig;
import com.zifeiweb.pzz.commen.Constants;
import com.zifeiweb.pzz.commen.RequestUrls;
import com.zifeiweb.pzz.utils.ProgressDialogUtil;
import com.zifeiweb.pzz.utils.Util;
import com.zifeiweb.pzz.webservice.WebService;

public class CookieListFragment extends Fragment implements OnItemClickListener{
	private ViewPager viewpager;
	private TextView num1,num2;
	private List<View> viewList=new ArrayList<View>();
	public static List<GoodsBean> list=new ArrayList<GoodsBean>();
	/**
	 * 一页的商品最大个数
	 */
	private final static int ONE_PAGE_NUM=9;
	/**
	 * 页数
	 */
	private int page_num=0;
	/**
	 * 最后一页的商品个数
	 */
	private int last_page_num=0;
	
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_cookie_list, null);
		this.context=inflater.getContext();
		
		init(view);
		
		return view;
	}
	
	public void init(View view){
		initView(view);
		initData(getArguments().getString("type"));
	}
	
	public void initView(View view){
		viewpager=(ViewPager)view.findViewById(R.id.viewpager);
		viewpager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				num1.setText(arg0+1+"");
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		num1=(TextView)view.findViewById(R.id.num1);
		num2=(TextView)view.findViewById(R.id.num2);
	}
	
	public void initData(String type){
		new WebService(context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Bundle _data = msg.getData();
				switch (msg.what) {
				case Constants.HTTP_REQUEST_START:
					pUtil.showProgressDialog("正在加载数据...");
					break;
				case Constants.HTTP_REQUEST_FAILED:
					Toast.makeText(context, "服务器返回异常，请重试", Toast.LENGTH_SHORT).show();
					pUtil.hideProgressDialog();
					break;
				case Constants.HTTP_RESULT_SUCCESS:
					pUtil.hideProgressDialog();
					JSONObject obj1;
					JSONArray ja1;
					try {
						if(_data.getString("url").endsWith(RequestUrls.GOODS_LIST)){
							obj1 = new JSONObject(_data.getString("json").toString());
							ja1=obj1.getJSONArray("data");
							list.clear();
							for(int i=0;i<ja1.length();i++){
								GoodsBean goodsBean=new GoodsBean();
								goodsBean.setGoods_id(ja1.getJSONObject(i).getString("goods_id"));
								goodsBean.setGoods_name(ja1.getJSONObject(i).getString("goods_name"));
								goodsBean.setGoods_remark(ja1.getJSONObject(i).getString("goods_remark"));
								goodsBean.setGoods_sn(ja1.getJSONObject(i).getString("goods_sn"));
								goodsBean.setOriginal_img(ja1.getJSONObject(i).getString("original_img"));
								goodsBean.setShop_price(ja1.getJSONObject(i).getString("shop_price"));
								list.add(goodsBean);
							}
							
							//分页
							page_num=(int)(list.size()/ONE_PAGE_NUM);
							if(list.size()%ONE_PAGE_NUM!=0){
								page_num=page_num+1;
							}
							//最后一页的数据条数
							if(list.size()%ONE_PAGE_NUM!=0){
								last_page_num=list.size()%ONE_PAGE_NUM;
							}else{
								last_page_num=ONE_PAGE_NUM;
							}
							viewList.clear();
							//为每页填充数据
							for(int i=0;i<page_num;i++){
								View view;
								if(i==page_num-1){
									view=LayoutInflater.from(context).inflate(R.layout.viewpager_content, null);
									GridView gridview=(GridView)view.findViewById(R.id.gridview);
									gridview.setAdapter(new MGridViewAdapter(context,i,last_page_num));
									gridview.setOnItemClickListener(CookieListFragment.this);
								}else{
									view=LayoutInflater.from(context).inflate(R.layout.viewpager_content, null);
									GridView gridview=(GridView)view.findViewById(R.id.gridview);
									gridview.setAdapter(new MGridViewAdapter(context,i,ONE_PAGE_NUM));
									gridview.setOnItemClickListener(CookieListFragment.this);
								}
								viewList.add(view);
							}
							if(viewList.size()==0){
								viewList.add(LayoutInflater.from(context).inflate(R.layout.viewpager_content, null));
							}
							viewpager.setAdapter(pagerAdapter);
							
							num1.setText("1");
							if(page_num==0){
								num2.setText("1");
							}else{
								num2.setText(page_num + "");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case Constants.HTTP_RESULT_UNSUCCESS:
					pUtil.hideProgressDialog();
					JSONObject obj2;
					try {
						obj2 = new JSONObject(_data.get("json").toString());
						Toast.makeText(context, obj2.getString("message"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		}).getGoodsList(type, new AppConfig(context).getShopId());
		//System.out.println("shopID:"+new AppConfig(context).getShopId()+";type:"+type);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
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
        public void destroyItem(ViewGroup container, int position,Object object) {  
            container.removeView(viewList.get(position));  
        }  
        @Override  
        public Object instantiateItem(ViewGroup container, int position) { 
            container.addView(viewList.get(position));  
            return viewList.get(position);  
        }  
    };  
    
    class MGridViewAdapter extends BaseAdapter{
    	Context context;
    	int index,num;
    	public MGridViewAdapter(Context context,int index,int num){
    		this.context=context;
    		this.index=index;
    		this.num=num;
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
			return position+index*ONE_PAGE_NUM;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			position=position+index*ONE_PAGE_NUM;
			ViewHolder viewHolder = null;
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=LayoutInflater.from(context).inflate(R.layout.goods_item, null);
				//动态设置这个高度即可。保证该高度为上一层view的高度的三分之一，就能刚好填满全屏
				convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Util.getWindowHeight(context)-Util.dip2px(context, getResources().getDimension(R.dimen.buttom_view_up_hight)+getResources().getDimension(R.dimen.gridview_gap)))/3));
				viewHolder.image=(ImageView)convertView.findViewById(R.id.image);
				viewHolder.name=(TextView)convertView.findViewById(R.id.name);
				viewHolder.price=(TextView)convertView.findViewById(R.id.price);
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			
			ImageLoader.getInstance().displayImage(AppConfig.URL_HTTP_IMG+list.get(position).getOriginal_img(), viewHolder.image);
			viewHolder.name.setText(list.get(position).getGoods_name());
			viewHolder.price.setText("￥"+list.get(position).getShop_price()+"/份");
			
			return convertView;
		}
		
		class ViewHolder{
			ImageView image;
			TextView name;
			TextView price;
		}
    }
}
