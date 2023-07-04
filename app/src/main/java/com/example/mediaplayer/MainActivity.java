package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;

import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);

        runtimePermission();


    }

    public void runtimePermission()
    {
        Dexter.withContext(this)/// Используем библиотеку декстер для того что бы получить доступ к файлам в устройстве
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)/// Получаем доступ для чтение файлов
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse)
                    {
                    displaySongs();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse)
                    {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong (File file) // Функция для нахождения файлов внутри устройства, после мы получаем масив из файлов с разширением "mp3,mp4,wav,wma,aac"
    {
        ArrayList<File> arrayList = new ArrayList<>(); /// Масив для хранения файлов

        File[] files = file.listFiles();  /// Получаем список файлов в деректорий

        for(File singleFile: files) /// Цикл для пойска нужных файлов с разшерением, проходимся по файлЛисту
        {
            if(singleFile.isDirectory() && !singleFile.isHidden()) /// Если файл являеться деректорией или файл не спрятанм добовляем масив
            {
             arrayList.addAll(findSong(singleFile)); /// Добовлние в масив с помошью add
            }
            else /// Если выбраный файл не являеться деректорией и он не спрятан мы проверяем его формат
            {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".mp4") || singleFile.getName().endsWith(".wma") || singleFile.getName().endsWith(".aac"))/// если данный формат файла нам ныжный то он так же добовляеться в масив
                {
                    arrayList.add(singleFile); /// Добавления с помошью add
                }
            }
        }
        return arrayList;/// После оканчания этой функций мы возврашяем уже готовый масив с файлами
    }
    void displaySongs()
    {
         final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory()); /// Создаем масив для получения файлов после вызываем функцию  findSong после передаем деректориюс с пмошью getExternalStorageDirectory

        items = new String[mySongs.size()];/// Создаем масив для хранения имен файлов

        for(int i = 0;i < mySongs.size();i++) ///Цикл для того что убрать формат файла что отображть без формата файла
        {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace("wav","").replace("mp4","").replace("wma", "").replace("aac","");/// используем функцию реплайс для замены символов
        }
        /*ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(myAdapter);*/

        customAdapter customAdapter = new customAdapter();

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .putExtra("pos",i));
            }
        });
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);

            return myView;
        }
    }
}