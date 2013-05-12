package br.com.redu.redumobile.activities;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WebCachedImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.StatusDetailAdapter;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.util.DateUtil;
import br.com.redu.redumobile.widgets.Breadcrumb;
import br.com.redu.redumobile.widgets.StatusComposer;
import br.com.redu.redumobile.widgets.StatusComposer.OnStatusComposerListener;

public class StatusDetailActivity extends BaseActivity {

	public static final String EXTRAS_STATUS = "EXTRAS_STATUS";

	private LayoutInflater mInflater;

	private ListView mListView;

	private StatusDetailAdapter mAdapter;
	private View mFooterLoadingAnswers;

	private Status mStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_status_detail);

		Bundle extras = getIntent().getExtras();
		mStatus = (Status) extras.get(EXTRAS_STATUS);

		setActionBarTitle(mStatus.getLastBreadcrumb());

		if (!mStatus.isPostedOnUserWall()) {
			addActionToActionBar(R.drawable.bt_go_to_wall,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent i = null;
							if (mStatus.isPostedOnLectureWall()) {
								i = new Intent(StatusDetailActivity.this,
										LectureActivity.class);
								i.putExtra(LectureActivity.EXTRAS_SUBJECT_ID,
										mStatus.getSubjectId());
								i.putExtra(LectureActivity.EXTRAS_LECTURE_ID,
										mStatus.getLectureId());

							} else if (mStatus.isPostedOnSpaceWall()) {
								i = new Intent(StatusDetailActivity.this,
										SpaceActivity.class);
								i.putExtra(SpaceActivity.EXTRAS_SPACE_ID,
										mStatus.getSpaceId());

								Bundle extras = new Bundle();
								extras.putString(
										SpaceActivity.EXTRAS_ENVIRONMENT_PATH,
										mStatus.getEnvironmentPath());
								extras.putString(SpaceActivity.EXTRAS_SPACE_ID,
										mStatus.getSpaceId());
								setUpClass(SpaceActivity.class, extras);

							} else if (mStatus.isPostedOnUserWall()) {
								setUpClass(HomeActivity.class);
							}

							startActivity(i);
						}
					});
		}

		mInflater = LayoutInflater.from(this);

		mFooterLoadingAnswers = mInflater.inflate(
				R.layout.status_detail_footer_loading_answers, null);

		mListView = (ListView) findViewById(R.id.list);
		mListView.addHeaderView(createStatusHeaderView(mStatus));

		((StatusComposer) findViewById(R.id.status_composer))
				.setOnStatusComposerListener(new OnStatusComposerListener() {
					@Override
					public void onSendClicked(String text) {
						new PostAnswerTask().execute(mStatus.id, text);
					}
				});

		mAdapter = new StatusDetailAdapter(getApplicationContext(), null);
		mListView.setAdapter(mAdapter);

		new LoadAnswersStatus(mStatus.id).execute();
	}

	private View createStatusHeaderView(Status status) {
		View v = mInflater.inflate(
				R.layout.status_detail_header_original_status, null);

		((Breadcrumb) v.findViewById(R.id.tv_breadcrumb)).setStatus(status);

		((WebCachedImageView) v.findViewById(R.id.iv_photo))
				.setImageUrl(status.user.getThumbnailUrl());
		((TextView) v.findViewById(R.id.tv_date)).setText(DateUtil
				.getFormattedStatusCreatedAt(status));

		StringBuffer userActionBuffer = new StringBuffer().append("<b>")
				.append(status.user.getCompleteName()).append("</b>");

		if (status.isActivityType()) {
			userActionBuffer.append(" comentou");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);

		} else if (status.isAnswerType()) {
			userActionBuffer.append(" comentou");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);

		} else if (status.isHelpType()) {
			userActionBuffer.append(" pediu ajuda");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.VISIBLE);
		}

		((TextView) v.findViewById(R.id.tv_user_action)).setText(Html
				.fromHtml(userActionBuffer.toString()));

		((TextView) v.findViewById(R.id.tv_text)).setText(status.text);

		return v;
	}

	// private View createLocalOfPublicationHeaderView(Status status) {
	// View v =
	// mInflater.inflate(R.layout.status_detail_header_publishing_local, null);
	//
	// Statusable statusable = status.getStatusable();
	//
	// if(statusable.isTypeUser()) {
	// v = null;
	//
	// } else if(statusable.isTypeLecture()) {
	// TextView tv = ((TextView) v.findViewById(R.id.tv_title));
	// tv.setText(statusable.name);
	// tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_aula_azul, 0, 0,
	// 0);
	//
	// } else if(statusable.isTypeSpace()) {
	// TextView tv = ((TextView) v.findViewById(R.id.tv_title));
	// tv.setText(statusable.name);
	// tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_disciplina_azul,
	// 0, 0, 0);
	// }
	//
	// return v;
	// }

	class LoadAnswersStatus extends
			AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {
		private String mStatusId;

		public LoadAnswersStatus(String statusId) {
			mStatusId = statusId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mListView.addFooterView(mFooterLoadingAnswers);
		}

		@Override
		protected List<br.com.developer.redu.models.Status> doInBackground(
				Void... params) {
			List<br.com.developer.redu.models.Status> answers;

			try {
				DefaultReduClient redu = ReduApplication
						.getReduClient(StatusDetailActivity.this);
				answers = redu.getAnswers(mStatusId);
			} catch (Exception e) {
				// TODO show message for internet problems
				e.printStackTrace();
				answers = null;
			}

			return answers;
		}

		@Override
		protected void onPostExecute(
				List<br.com.developer.redu.models.Status> answers) {
			if (answers != null) {
				if (answers.size() > 0) {
					DbHelper dbHelper = DbHelper
							.getInstance(StatusDetailActivity.this);
					dbHelper.updateStatusAnswersCount(mStatusId, answers.size());

					mAdapter.addAll(answers);
					mAdapter.notifyDataSetChanged();

					mListView.removeFooterView(mFooterLoadingAnswers);
				} else {
					mFooterLoadingAnswers.findViewById(R.id.pb).setVisibility(View.GONE);
					((TextView) mFooterLoadingAnswers
							.findViewById(R.id.tv_text))
							.setText("Não há respostas, seja o primeiro a responder");
				}
			} else {
				// TODO handler no internet problem connection
			}

		}
	}

	class PostAnswerTask extends
			AsyncTask<String, Void, br.com.developer.redu.models.Status> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("Enviando sua resposta…");
		}

		@Override
		protected br.com.developer.redu.models.Status doInBackground(
				String... params) {
			String statusId = params[0];
			String text = params[1];

			br.com.developer.redu.models.Status status;
			try {
				DefaultReduClient redu = ReduApplication
						.getReduClient(StatusDetailActivity.this);
				status = redu.postAnswer(statusId, text);
			} catch (Exception e) {
				status = null;
			}

			return status;
		}

		@Override
		protected void onPostExecute(br.com.developer.redu.models.Status result) {
			dismissProgressDialog();

			if (result == null) {
				Toast.makeText(StatusDetailActivity.this,
						"Não foi possível enviar sua resposta.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(StatusDetailActivity.this,
						"Resposta enviada com sucesso.", Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		}
	}
}
