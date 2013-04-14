package br.com.redu.redumobile.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.activities.StatusDetailActivity;
import br.com.redu.redumobile.adapters.StatusWallAdapter;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;
import br.com.redu.redumobile.db.DbHelperListener;
import br.com.redu.redumobile.tasks.LoadStatusesFromWebTask;

public class HomeFragment extends Fragment implements DbHelperListener {

	public enum Type {LastSeen, Wall, NewLectures};

	private static final int NUM_STATUS_BY_PAGE = 25;
	
	private ProgressBar mProgressBar;
	
	private StatusWallAdapter mAdapter;

	private long mTimestamp;
	private boolean mUpdatingList;

	private Type mType;
	
	public HomeFragment() {

	}
	
	public String getTitle() {
		switch (mType) {
			case Wall:
				return "Início";
			
			case NewLectures:
				return "Novas Aulas";
				
			case LastSeen:
				return "Últimos Visualizados";
	
			default:
				return null;
		}
	}
	
	@Override
	public void setArguments(Bundle bundle) {
		mType = (Type) bundle.get(Type.class.getName());
	}
	
	@Override
	public void hasNewStatus() {
		updateStatuses();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_listview, container, false);

		mProgressBar = (ProgressBar) v.findViewById(R.id.pb);
		
		if(mAdapter == null) {
			mAdapter = new StatusWallAdapter(getActivity());
			mTimestamp = System.currentTimeMillis();
		}
		
		ListView lv = (ListView) v.findViewById(R.id.list);
		lv.setAdapter(mAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Status status = (Status) mAdapter.getItem(position);
				
				if(!status.isLogType()) {
					Intent i = new Intent(getActivity(), StatusDetailActivity.class);
					i.putExtra(StatusDetailActivity.EXTRAS_STATUS, status);
					startActivity(i);
					
					DbHelper dbHelper = ((DbHelperHolder) getActivity()).getDbHelper();
					dbHelper.setStatusAsLastSeen(status);
				}
			}
		});
		
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, 
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount == totalItemCount 
						&& totalItemCount != 0) {
					updateStatuses();
				}
			}
		});

		updateStatuses();

		return v;
	}

	private void updateStatuses() {
		if (!mUpdatingList) {
			new LoadStatusesFromDbTask().execute();
		}
	}
	
	class LoadStatusesFromDbTask extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {

		protected void onPreExecute() {
			mUpdatingList = true;
		};

		protected List<br.com.developer.redu.models.Status> doInBackground(Void... params) {
			List<br.com.developer.redu.models.Status> statuses = null;
			
			Activity activity = getActivity();
			
			if(activity != null && activity instanceof DbHelperHolder && mType != null) {
				DbHelper dbHelper = ((DbHelperHolder) activity).getDbHelper();
				
				switch (mType) {
					case LastSeen:
						statuses = dbHelper.getLastSeenStatus(mTimestamp, NUM_STATUS_BY_PAGE);
						break;
					
					case Wall:
						statuses = dbHelper.getStatus(mTimestamp, NUM_STATUS_BY_PAGE);
						break;
						
					case NewLectures:
						statuses = dbHelper.getNewLecturesStatus(mTimestamp, NUM_STATUS_BY_PAGE);
						break;
				
					default:
						statuses = null;
						break;
				}
			}
			
			return statuses;
		}

		protected void onPostExecute(List<br.com.developer.redu.models.Status> statuses) {
			 if(getActivity() != null) {
				 if (statuses != null && statuses.size() > 0) {
					mAdapter.addAll(statuses);
					mAdapter.notifyDataSetChanged();
					mTimestamp = statuses.get(statuses.size()-1).created_at_in_millis;
					mProgressBar.setVisibility(View.GONE);
					
				} else if(!LoadStatusesFromWebTask.isWorking()) {
					mProgressBar.setVisibility(View.GONE);
				
				} else {
					Toast.makeText(getActivity(), "Aguarde alguns instantes…", Toast.LENGTH_SHORT).show();
				}
			}
			
			mUpdatingList = false;
		};
	}
}
