# ArcMenu
添加自己喜欢的布局文件  上下左右四个角，只需修改自定义属性 positon  和radius 
 * eg、左下角


```xml
    <com.ayit.arcmenu.ArcMenu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:menu="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    menu:position="left_bottom"
    menu:radius="150dp">

    <RelativeLayout
        android:id="@+id/menuBtn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/composer_button">

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:background="@drawable/composer_icn_plus" />

    </RelativeLayout>

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/composer_camera"
        android:tag="Camera" />

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/composer_music"
        android:tag="Music" />

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/composer_place"
        android:tag="Place" />

</com.ayit.arcmenu.ArcMenu>

```

#在Activity 中只需设置监听函数 便可以捕获每个按钮的状态  
 arcMenu.setOnItemClickListener(new ArcMenu.OnmenuItemClickListener() {
            @Override
            public void onClick(View v, int pos) {
                Toast.makeText(MainActivity.this,v.getTag()+"_"+pos,Toast.LENGTH_SHORT).show();
            }
        });
        
    
