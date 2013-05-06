package br.com.redu.redumobile.adapters;

import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.sax.StartElementListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.activities.lecture.NewFolderActivity;
import br.com.redu.redumobile.activities.lecture.UploadStep1Activity;
import br.com.redu.redumobile.activities.lecture.UploadStep2Activity;
import br.com.redu.redumobile.activities.lecture.UploadStep3Activity;

public class PopupAdapter extends BaseAdapter {

	final private LayoutInflater mInflater;
	final private Context mContext;
	private Space space;
	private String id;
	private String[] values;
	
	public PopupAdapter(Context context, String[] values, String id, Space space) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.values = values;
		this.id = id;
		this.space = space;
	}
	
	@Override
	public int getCount() {
		return values.length;
	}

	@Override
	public Object getItem(int position) {
		return values[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
//	public void add(TextView status) {
//		listMaterials.add(status);
//	}
//
//	public void add(List<TextView> statuses) {
//		listMaterials.addAll(statuses);
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout ll = (LinearLayout) mInflater.inflate(R.layout.popup_listview_row, null);
		ImageView iv = (ImageView)ll.findViewById(R.id.iv_insert_file_folder);
		TextView tv = (TextView)ll.findViewById(R.id.tv_insert_file_folder);
		tv.setText(values[position]);
		if (values[position].equals("Arquivo de Apoio")){
			iv.setImageResource(R.drawable.ic_file_mini);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, UploadStep1Activity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					mContext.startActivity(it);
				}
			});
		}
		if (values[position].equals("Pasta")){
			iv.setImageResource(R.drawable.ic_folder);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, NewFolderActivity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					mContext.startActivity(it);
				}
			});
		}
		if (values[position].equals("Vídeo")){
			iv.setImageResource(R.drawable.ic_midia);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, UploadStep2Activity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					it.putExtra("type", "video");
					mContext.startActivity(it);
				}
			}); 
		}
		if (values[position].equals("Foto")){
			iv.setImageResource(R.drawable.ic_photo);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, UploadStep2Activity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					it.putExtra("type", "foto");
					mContext.startActivity(it);
				}
			});  
		}
		if (values[position].equals("Áudio")){
			iv.setImageResource(R.drawable.ic_midia);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, UploadStep2Activity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					it.putExtra("type", "audio");
					mContext.startActivity(it);
				}
			}); 
		}
		if (values[position].equals("Gravar")){
			iv.setImageResource(R.drawable.ic_midia);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, UploadStep3Activity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					mContext.startActivity(it);
				}
			}); 
		}
		if (values[position].equals("Escolher da Galeria")){
			iv.setImageResource(R.drawable.ic_galery);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(mContext, UploadStep3Activity.class);
					it.putExtra(Space.class.getName(), space);
					it.putExtra("id", id);
					mContext.startActivity(it);
				}
			}); 
		}
		return ll;
	}
}
