package br.com.redu.redumobile.activities.lecture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.activities.LectureActivity;
import br.com.redu.redumobile.activities.SpaceActivity;
import br.com.redu.redumobile.adapters.PopupAdapter;

public class UploadStep2Activity extends Activity {

	String superId;
	Space space;
	String type;
	private String selectedImagePath;
	private Subject mSubject;
	
	AlertDialog mDialog;
	private AlertDialog dialogError;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_file_or_lecture);
		TextView tvTitle = (TextView)findViewById(R.id.tvTitleUpload2);
		tvTitle.setText("Escolha a forma de adição?");
		superId = getIntent().getExtras().getString("id");
		type = getIntent().getExtras().getString("type");
		space = (Space)getIntent().getExtras().get(Space.class.getName());
		mSubject = (Subject)getIntent().getExtras().get(Subject.class.getName());
		ListView lv = (ListView)findViewById(R.id.lvInsertFileFolder);
		String[] str = new String[2];
		if (type.equals("audio")){
			str[0] =  "Gravar";
			str[1] =  "Escolher Áudio";
		}else{
			str[0] =  "Camera";
			str[1] =  "Escolher da Galeria";
		}
		if (mSubject != null) {
			lv.setAdapter(new PopupAdapter(this, str, space, mSubject));
		}else{
			lv.setAdapter(new PopupAdapter(this, str, superId, space));
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// If it's a recording action
				if (position == 0) {
					if (type.equals("foto")) {
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent, 2);
					}else if(type.equals("video")){
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
						
						startActivityForResult(cameraIntent, 2);
					}else{
						Intent cameraIntent = new Intent(android.provider.MediaStore.Audio.Media.RECORD_SOUND_ACTION);
						try {
							startActivityForResult(cameraIntent, 2);
						} catch (ActivityNotFoundException e) {
							Builder builder = new AlertDialog.Builder(UploadStep2Activity.this);
							builder.setMessage("Nenhuma aplicação para Áudio encontrada. Você deve instalar alguma aplicação no seu aparelho capaz de processar Áudio.");
				        	builder.setCancelable(true);
				        	mDialog = builder.create();
				        	mDialog.show();
						}
					}
					
				}
				// If it's a selection action
				if (position == 1) {
					if (type.equals("foto")) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Escolha a Imagem"), 2);
					}
					if (type.equals("video")) {
						Intent intent = new Intent();
						intent.setType("video/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Escolha o Video"), 2);
					}
					if (type.equals("audio")) {
						Intent intent = new Intent();
						intent.setType("audio/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						try {
							startActivityForResult(Intent.createChooser(intent, "Escolha o Audio"), 2);
						} catch (ActivityNotFoundException e) {
							Builder builder = new AlertDialog.Builder(UploadStep2Activity.this);
							builder.setMessage("Nenhuma aplicação para Áudio encontrada. Você deve instalar alguma aplicação no seu aparelho capaz de processar Áudio.");
				        	builder.setCancelable(true);
				        	mDialog = builder.create();
				        	mDialog.show();
						} catch (Exception e) {
							Builder builder = new AlertDialog.Builder(UploadStep2Activity.this);
							builder.setMessage("Erro ao tentar gravar Áudio, tente novamente.");
				        	builder.setCancelable(true);
				        	mDialog = builder.create();
				        	mDialog.show();
						}
						
					}
				}
			}
		});
	}
	
	/*@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SpaceActivity.REQUEST_CODE_LECTURE){
				setResult(Activity.RESULT_OK, data);
				finish();
			}
	        if(requestCode == 2) {
	        	if (type.equals("foto")){
	        		Uri selectedImageUri = data.getData();
	                //OI FILE Manager
	        		if (selectedImageUri == null){
	        			Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Este recurso está desabilitado para seu modelo de aparelho. Tente usar a opção \"Escolher da Galeria\"");
			        	builder.setCancelable(true);
			        	dialogError = builder.create();
			        	dialogError.show();
	        		}else{
	        			//MEDIA GALLERY
		                selectedImagePath = getPath(selectedImageUri);
			            /*mRlayoutimage.setBackgroundDrawable(drawable);*/
			            Intent it = new Intent(this, UploadStep3Activity.class);
			    		it.putExtra(Space.class.getName(), space);
			    		it.putExtra(Subject.class.getName(), mSubject);
			    		it.putExtra("id", superId);
			    		it.putExtra("foto", selectedImagePath);
			    		it.putExtra("type", type);
			    		startActivityForResult(it, SpaceActivity.REQUEST_CODE_LECTURE);
	        		}
	        	}
	        	if (type.equals("video")){
	        		Uri uriVideo = data.getData();
	        		
	        		if (uriVideo == null){
	        			Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Este recurso está desabilitado para seu modelo de aparelho. Tente usar a opção \"Escolher da Galeria\"");
			        	builder.setCancelable(true);
			        	dialogError = builder.create();
			        	dialogError.show();
	        		}else{
	        			Log.i("ARQUIVO", getPath(uriVideo));
		        		
		        		Intent it = new Intent(this, UploadStep3Activity.class);
				    	it.putExtra(Space.class.getName(), space);
				    	it.putExtra(Subject.class.getName(), mSubject);
				    	it.putExtra("id", superId);
				    	it.putExtra("video", getPath(uriVideo));
				    	it.putExtra("type", type);
				    	startActivityForResult(it, SpaceActivity.REQUEST_CODE_LECTURE);
	        		}
	        	}	
		    	if (type.equals("audio")){
	        		Uri uriAudio = data.getData();
	        		if (uriAudio == null){
	        			Builder builder = new AlertDialog.Builder(this);
						builder.setMessage("Este recurso está desabilitado para seu modelo de aparelho. Tente usar a opção \"Escolher da Galeria\"");
			        	builder.setCancelable(true);
			        	dialogError = builder.create();
			        	dialogError.show();
	        		}else{
	        			//Log.i("Audio", getPath(uriAudio));
		        		
		        		Intent itAudio = new Intent(this, UploadStep3Activity.class);
		        		itAudio.putExtra(Space.class.getName(), space);
		        		itAudio.putExtra(Subject.class.getName(), mSubject);
		        		itAudio.putExtra("id", superId);
		        		itAudio.putExtra("video", getPath(uriAudio));
		        		itAudio.putExtra("type", type);
		        		startActivityForResult(itAudio, SpaceActivity.REQUEST_CODE_LECTURE);
	        		}
		    	}
	        }
	    }
	}
	
	//UPDATED!
    @SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
}

