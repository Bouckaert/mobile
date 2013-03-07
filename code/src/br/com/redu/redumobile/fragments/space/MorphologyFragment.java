package br.com.redu.redumobile.fragments.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.SubjectExpandableListAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.AbsListView.OnScrollListener;

public class MorphologyFragment extends Fragment {
	
	private User mUser;
	private int mCurrentPage;
	private boolean mUpdatingList;
	private Space mSpace;
	
	private List<Subject> mEnrollmentedSubjects;
	private List<List<Lecture>> mLecture;
	
	private ExpandableListView mExpListView;
	private SubjectExpandableListAdapter mAdapter;
	
	
	public MorphologyFragment() {
		mCurrentPage = 0;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.fragment_mophology, container, false);
		
		mSpace = (Space)getActivity().getIntent().getExtras().get(Space.class.getName());
		
		mExpListView = (ExpandableListView) v.findViewById(R.id.elvSubject);
		mExpListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(!mUpdatingList && firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
					new LoadSubjectsTask(mCurrentPage++).execute();
				}
			}
		});
		
		new LoadUserTask().execute();
		
		return v;
	}
	
	class LoadUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			Log.i("Redu", redu.getAuthorizeUrl());
			return redu.getMe();
		}
	
		protected void onPostExecute(User user) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
			mUser = user;

			new LoadSubjectsTask(mCurrentPage).execute();
		};
	}
	
	/*class LoadCoursesTask extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Course>> {
		protected List<br.com.developer.redu.models.Course> doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			List<br.com.developer.redu.models.Environment> environments = redu.getEnvironments();
			return redu.getCoursesByEnvironment(environments.get(0).id);
		}
	
		protected void onPostExecute(User user) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
			mUser = user;

			new LoadSubjectsTask(mCurrentPage).execute();
		};
	}*/
	
	class LoadSubjectsTask extends AsyncTask<Void, Void, Void> {

		private int page;
		
		public LoadSubjectsTask(int page) {
			this.page = page;
		}
		
		protected void onPreExecute() {
			mUpdatingList = true;
		};
		
		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			mEnrollmentedSubjects = new ArrayList<Subject>();
			List<Subject> subjects = new ArrayList<Subject>();
			subjects = redu.getSubjectsBySpace(mSpace.id);
			
			mLecture = new ArrayList<List<Lecture>>();
			
			if(subjects != null) {
				subjects.removeAll(Collections.singleton(null));
				for(Subject subject : subjects) {
					List<Lecture> lectureBySubject;
					try {
						lectureBySubject = redu.getLecturesBySubject(subject.id);
					} catch(OAuthConnectionException e) {
						// usuario nao matriculado no curso
						e.printStackTrace();
						lectureBySubject = null;
					}

					if(lectureBySubject != null) {
						mEnrollmentedSubjects.add(subject);
						mLecture.add(lectureBySubject);
					}
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			mAdapter = new SubjectExpandableListAdapter(getActivity(), mEnrollmentedSubjects, mLecture);
			mExpListView.setAdapter(mAdapter);			
			mUpdatingList = false;
		};
	}
}
