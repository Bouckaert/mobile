package br.com.redu.redumobile.activities.lecture;

import br.com.developer.redu.models.Folder;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.PopupAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class UploadFileFolderActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_file_or_lecture);
		ListView lv = (ListView)findViewById(R.id.lvInsertFileFolder);
		String[] str = {"Arquivo de Apoio","Pasta"};
		String id = (String)getIntent().getExtras().getString("id");
		Space space = (Space)getIntent().getExtras().get(Space.class.getName());
		lv.setAdapter(new PopupAdapter(this, str, id, space));
	}
}
