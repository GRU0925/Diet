package com.example.diet_master;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoloActivity extends AppCompatActivity {

    final private static String TAG = "YoloActivity";
    Button btn_photo, btn_addFood;
    ImageView iv_photo;
    TextView addIngredients;
    DatePicker date;
    Spinner addType;
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    String yolo_name="";
    private Spinner MealType;

    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net Yolov3;
    private final int GET_GALLERY_IMAGE = 200;

    // FirebaseStore
    FirebaseFirestore dbStore = FirebaseFirestore.getInstance();

    // view
    TextView tvYoloCalory, tvYoloCarb, tvYoloProtein, tvYoloFat;
    Button btAddFood;

    // Spinner
    Spinner yoloAmount;
    String[] spItems = {"1/4 인분", "1/2 인분", "3/4 인분", "1 인분", "2 인분", "3 인분"}; //식사량 선택

    String[] spmeal = {"아침", "점심", "저녁"}; //식사 시간 선택
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterMeal;
    double calAmount;

    // DB
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference;
    private FirebaseAuth auth;  // firebase auth
    private FirebaseUser currentUser;  //firebase user
    String uid;
    DatabaseReference db;

    // Date
    Date currentDate;
    SimpleDateFormat dateFormat;
    String dbDate;

    // result
    double calory, carb, protein, fat;
    String sCalory, sCarb, sProtein, sFat;
    String foodName;

    // ThreeMeal
    String threeMeal;

    // Image 파이어베이스 업로드
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Bitmap bitmap;

    //AddYoloFood addYoloFood = new AddYoloFood(getApplicationContext());


    //assets 파일 가져오기
    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            return outFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void YOLO() {
        if (startYolo == false) {
            startYolo = true;
            if (firstTimeYolo == false) {
                firstTimeYolo = true;
                String yoloCfg = getPath("yolov3-tiny.cfg", this); //assets의 파일 정보를 받아와 패치
                String yoloWeights = getPath("yolov3-tiny.weights", this); // assets의 파일 정보를 받아와 패치

                Yolov3 = Dnn.readNetFromDarknet(yoloCfg, yoloWeights);
            }

        } else {
            startYolo = false;
        }
    }


    // 욜로 이미지 디텍션&화면에 출력
    public void detect_food(Bitmap bitmap){
        Mat frame = new Mat();
        Utils.bitmapToMat(bitmap, frame);

        //Imgproc을 이용해 이미지 프로세싱
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);//rgba 체계를 rgb로 변경해야 흑백이아닌 컬러 화면으로 찍힘

        Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0),false, false);
        //뉴런 네트워크에 이미지 넣기

        Yolov3.setInput(imageBlob);

        //cfg 파일에서 yolo layer number을 확인하여 이를 순전파에 넣어준다.
        //yolv3-tiny는 yolo layer가 2개라서 initialCapacity를 2로 준다. yolov3와 yolov4는 layer가 4개
        List<Mat> result = new ArrayList<Mat>(2);

        List<String> outBlobNames = new ArrayList<>();
        outBlobNames.add(0, "yolo_16");
        outBlobNames.add(1, "yolo_23");

        //순전파를 진행
        Yolov3.forward(result, outBlobNames);

        //30%이상의 확률만 출력해준다.
        float confThreshold = 0.3f;

        //class id
        List<Integer> clsIds = new ArrayList<>();
        //
        List<Float> confs = new ArrayList<>();
        //draw rectanglelist
        List<Rect> rects = new ArrayList<>();


        for (int i = 0; i < result.size(); ++i) {

            Mat level = result.get(i);

            for (int j = 0; j < level.rows(); ++j) { //iterate row
                Mat row = level.row(j);
                Mat scores = row.colRange(5, level.cols());

                Core.MinMaxLocResult mm = Core.minMaxLoc(scores);


                float confidence = (float) mm.maxVal;

                //여러개의 클래스들 중에 가장 정확도가 높은(유사한) 클래스 아이디를 찾아낸다.
                Point classIdPoint = mm.maxLoc;


                if (confidence > confThreshold) {
                    int centerX = (int) (row.get(0, 0)[0] * frame.cols());
                    int centerY = (int) (row.get(0, 1)[0] * frame.rows());
                    int width = (int) (row.get(0, 2)[0] * frame.cols());
                    int height = (int) (row.get(0, 3)[0] * frame.rows());


                    int left = centerX - width / 2;
                    int top = centerY - height / 2;

                    clsIds.add((int) classIdPoint.x);
                    confs.add((float) confidence);


                    rects.add(new Rect(left, top, width, height));
                }
            }
        }
        int ArrayLength = confs.size();

        if (ArrayLength >= 1) {
            // Apply non-maximum suppression procedure.
            float nmsThresh = 0.2f;


            MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));


            Rect[] boxesArray = rects.toArray(new Rect[0]);

            MatOfRect boxes = new MatOfRect(boxesArray);

            MatOfInt indices = new MatOfInt();


            Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);


            // Draw result boxes:
            int[] ind = indices.toArray();
            for (int i = 0; i < ind.length; ++i) {

                int idx = ind[i];
                Rect box = boxesArray[idx];

                int idGuy = clsIds.get(idx);

                float conf = confs.get(idx);

                List<String> cocoNames = Arrays.asList(//욜로에 학습한 음식 클래스명
                        "rice",
                        "eels on rice",
                        "pilaf",
                        "chicken-'n'-egg on rice",
                        "pork cutlet on rice",
                        "beef curry",
                        "sushi",
                        "chicken rice",
                        "fried rice",
                        "tempura bowl",
                        "bibimbap",
                        "toast",
                        "croissant",
                        "roll bread",
                        "raisin bread",
                        "chip butty",
                        "hamburger",
                        "pizza",
                        "sandwiches",
                        "udon noodle",
                        "tempura udon",
                        "soba noodle",
                        "ramen noodle",
                        "beef noodle",
                        "tensin noodle",
                        "fried noodle",
                        "spaghetti",
                        "Japanese-style pancake",
                        "takoyaki",
                        "gratin",
                        "sauteed vegetables",
                        "croquette",
                        "grilled eggplant",
                        "sauteed spinach",
                        "vegetable tempura",
                        "miso soup",
                        "potage",
                        "sausage",
                        "oden",
                        "omelet",
                        "ganmodoki",
                        "jiaozi",
                        "stew",
                        "teriyaki grilled fish",
                        "fried fish",
                        "grilled salmon",
                        "salmon meuniere",
                        "sashimi",
                        "grilled pacific saury",
                        "sukiyaki",
                        "sweet and sour pork",
                        "lightly roasted fish",
                        "steamed egg hotchpotch",
                        "tempura",
                        "fried chicken",
                        "sirloin cutlet",
                        "nanbanzuke",
                        "boiled fish",
                        "seasoned beef with potatoes",
                        "hambarg steak",
                        "steak",
                        "dried fish",
                        "ginger pork saute",
                        "spicy chili-flavored tofu",
                        "yakitori",
                        "cabbage roll",
                        "omelet",
                        "egg sunny-side up",
                        "natto",
                        "cold tofu",
                        "egg roll",
                        "chilled noodle",
                        "stir-fried beef and peppers",
                        "simmered pork",
                        "boiled chicken and vegetables",
                        "sashimi bowl",
                        "sushi bowl",
                        "fish-shaped pancake with bean jam",
                        "shrimp with chill source",
                        "roast chicken",
                        "steamed meat dumpling",
                        "omelet with fried rice",
                        "cutlet curry",
                        "spaghetti meat sauce",
                        "fried shrimp",
                        "potato salad",
                        "green salad",
                        "macaroni salad",
                        "Japanese tofu and vegetable chowder",
                        "pork miso soup",
                        "chinese soup",
                        "beef bowl",
                        "kinpira-style sauteed burdock",
                        "rice ball",
                        "pizza toast",
                        "dipping noodles",
                        "hot dog",
                        "french fries",
                        "mixed rice",
                        "goya chanpuru",
                        "green curry",
                        "okinawa soba",
                        "mango pudding",
                        "almond jelly",
                        "jjigae",
                        "dak galbi",
                        "dry curry",
                        "kamameshi",
                        "rice vermicelli",
                        "paella",
                        "tanmen",
                        "kushikatu",
                        "yellow curry",
                        "pancake",
                        "champon",
                        "crape",
                        "tiramisu",
                        "waffle",
                        "rare cheese cake",
                        "shortcake",
                        "chop suey",
                        "twice cooked pork",
                        "mushroom risotto",
                        "samul",
                        "zoni",
                        "french toast",
                        "fine white noodles",
                        "minestrone",
                        "pot au feu",
                        "chicken nugget",
                        "namero",
                        "french bread",
                        "rice gruel",
                        "broiled eel bowl",
                        "clear soup",
                        "yudofu",
                        "mozuku",
                        "inarizushi",
                        "pork loin cutlet",
                        "pork fillet cutlet",
                        "chicken cutlet",
                        "ham cutlet",
                        "minced meat cutlet",
                        "thinly sliced raw horsemeat",
                        "bagel",
                        "scone",
                        "tortilla",
                        "tacos",
                        "nachos",
                        "meat loaf",
                        "scrambled egg",
                        "rice gratin",
                        "lasagna",
                        "Caesar salad",
                        "oatmeal",
                        "fried pork dumplings served in soup",
                        "oshiruko",
                        "muffin",
                        "popcorn",
                        "cream puff",
                        "doughnut",
                        "apple pie",
                        "parfait",
                        "fried pork in scoop",
                        "lamb kebabs",
                        "dish consisting of stir-fried potato, eggplant and green pepper",
                        "roast duck",
                        "hot pot",
                        "pork belly",
                        "xiao long bao",
                        "moon cake",
                        "custard tart",
                        "beef noodle soup",
                        "pork cutlet",
                        "minced pork rice",
                        "fish ball soup",
                        "oyster omelette",
                        "glutinous oil rice",
                        "trunip pudding",
                        "stinky tofu",
                        "lemon fig jelly",
                        "khao soi",
                        "Sour prawn soup",
                        "Thai papaya salad",
                        "boned, sliced Hainan-style chicken with marinated rice",
                        "hot and sour, fish and vegetable ragout",
                        "stir-fried mixed vegetables",
                        "beef in oyster sauce",
                        "pork satay",
                        "spicy chicken salad",
                        "noodles with fish curry",
                        "Pork Sticky Noodles",
                        "Pork with lemon",
                        "stewed pork leg",
                        "charcoal-boiled pork neck",
                        "fried mussel pancakes",
                        "Deep Fried Chicken Wing",
                        "Barbecued red pork in sauce with rice",
                        "Rice with roast duck",
                        "Rice crispy pork",
                        "Wonton soup",
                        "Chicken Rice Curry With Coconut",
                        "Crispy Noodles",
                        "Egg Noodle In Chicken Yellow Curry",
                        "coconut milk soup",
                        "pho",
                        "Hue beef rice vermicelli soup",
                        "Vermicelli noodles with snails",
                        "Fried spring rolls",
                        "Steamed rice roll",
                        "Shrimp patties",
                        "ball shaped bun with pork",
                        "Coconut milk-flavored crepes with shrimp and beef",
                        "Small steamed savory rice pancake",
                        "Glutinous Rice Balls",
                        "loco moco",
                        "haupia",
                        "malasada",
                        "laulau",
                        "spam musubi",
                        "oxtail soup",
                        "adobo",
                        "lumpia",
                        "brownie",
                        "churro",
                        "jambalaya",
                        "nasi goreng",
                        "ayam goreng",
                        "ayam bakar",
                        "bubur ayam",
                        "gulai",
                        "laksa",
                        "mie ayam",
                        "mie goreng",
                        "nasi campur",
                        "nasi padang",
                        "nasi uduk",
                        "babi guling",
                        "kaya toast",
                        "bak kut teh",
                        "curry puff",
                        "chow mein",
                        "zha jiang mian",
                        "kung pao chicken",
                        "crullers",
                        "eggplant with garlic sauce",
                        "three cup chicken",
                        "bean curd family style",
                        "salt & pepper fried shrimp with shell",
                        "baked salmon",
                        "braised pork meat ball with napa cabbage",
                        "winter melon soup",
                        "steamed spareribs",
                        "chinese pumpkin pie",
                        "eight treasure rice",
                        "hot & sour soup");

                //해당 클래스의 한국 음식명
                List<String> cocoNamesKR = Arrays.asList("쌀밥", "장어덮밥", "필라프", "치킨마요덮밥", "돈가스덮밥", "소고기카레", "초밥", "치킨라이스", "볶음밥", "튀김덮밥", "비빔밥","토스트","크로와상","롤빵","건포도빵","칩버티",
                        "햄버거","피자","샌드위치","우동","튀김우동","소바","라멘","고기국수","텐신국수","볶음국수","스파게티","팬케이크","문어빵","그라탕","야채볶음","크로켓","가지구이","시금치볶음","야채튀김","된장국","포타주","소세지",
                        "오뎅","오믈렛","두부튀김","만두","스튜","생선조림","생선까스","연어구이","연어스테이크","생선회","꽁치구이","스키야키","탕수육","생선구이","계란찜","튀김","치킨","소고기돈가스","난반즈케","고등어조림","소고기감자조림",
                        "함박스테이크","소고기스테이크","노가리","돼지고기볶음","마파두부","꼬치","양배추롤","오믈렛","계란후라이","낫토","연두부","계란말이","냉면","소고기고추볶음","돼지고기조림","삼계탕","광어회","연어회","붕어빵","칠리새우","통닭","고기찜만두",
                        "오므라이스","카레돈가스","미트스파게티","새우튀김","감자샐러드","야채샐러드","마카로니샐러드","야채두부스프","돈지루","중국식스프","소고기볼","우엉볶음","주먹밥","피자토스트","메밀소바","핫도그","감자튀김","잡곡밥","찬푸루",
                        "그린커리","오키나와소바","망고푸딩","행인두부","찌개","닭갈비","드라이커리","돌솥밥","쌀국수","빠에야","탄탄멘","쿠시카츠","옐로커리","팬케이크","짬뽕","크레페","티라미수","와플","치즈케이크","쇼트케이크","찹수이","회과육","버섯리조또", "?",
                        "떡국","프렌치토스트","국수","오므라이스","포토푀","치킨너겟","나메로","바게트","죽","장어덮밥","맑은국","유도후","미역볶음","유부초밥","등심돈가스","돈가스","치킨가스","햄돈가스","멘치카츠","말고기","베이글","스콘","또띠아","타코","나초",
                        "떡갈비","스크램블에그","밥그라탕","라자냐","시저샐러드","오트밀","튀긴고기만두","단팥죽","머핀","팝콘","프로피트롤","도넛","애플파이","파르페","꿔바로우","케밥","감자볶음","오리구이","훠궈","삼겹살","샤오룽바오","월병","에그타르트",
                        "소고기국수","돈가스","루러우판","오뎅탕","굴오믈렛","찹쌀밥","무조림","취두부","무화과","카오소이","새우스프","파파야샐러드","닭고기양념밥","라구","야채복음","돼지불고기","돼지꼬치구이","치킨샐러드","생선카레국수","찹쌀국수","레몬돼지고기",
                        "족발","돼지목살","홍합볶음","버팔로윙","제육덮밥","오리고기덮밥","수육","완탕","치킨카레라이스","볶음면","치킨카레국수","코코넛스프","베트남쌀국수","소고기쌀국수","당면","에그롤","딤섬","새우전","고기만두","반쎄오","쌀케이크","찹쌀떡","로코모코","하우피아크림파이",
                        "하와이도넛","월남쌈","스팸주먹밥","꼬리곰탕","닭볶음탕","춘권","브라우니","츄러스","토마토리조또","나시고렝","닭튀김","숯불치킨","닭죽");
                int intConf = (int) (conf * 100);

                Imgproc.putText(frame, cocoNames.get(idGuy) + " " + intConf + "%", box.tl(), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0), 2);

                addIngredients.setText(cocoNamesKR.get(idGuy)); // 해당 음식의 이미지 클래스가 인식되면 음식을 한국명으로 설정
                foodName = addIngredients.getText().toString(); // 음식의 이름을 addIngredients 처리

                Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(255, 0, 0), 2);

                getFoodData();
            }

        }

        Utils.matToBitmap(frame, bitmap);
        iv_photo.setImageBitmap(bitmap);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        MealType = findViewById(R.id.SP_index);
        selectmeal();


        // View
        tvYoloCalory = (TextView)findViewById(R.id.TV_yoloCalory);
        tvYoloCarb = (TextView)findViewById(R.id.TV_yoloCarb);
        tvYoloProtein = (TextView)findViewById(R.id.TV_yoloProtein);
        tvYoloFat = (TextView)findViewById(R.id.TV_yoloFat);
        btAddFood = (Button) findViewById(R.id.btn_addFood);

        currentUser = auth.getInstance().getCurrentUser();
        uid = auth.getInstance().getUid();
        dbReference = database.getReference();

        // CurrentUser
        currentUser = auth.getInstance().getCurrentUser();
        uid = auth.getInstance().getUid();
        dbReference = database.getReference();

        // date
        long curTime = System.currentTimeMillis();
        currentDate = new Date(curTime);
        dateFormat = new SimpleDateFormat("yyMMdd");
        dbDate = dateFormat.format(currentDate);

        // Spinner
        yoloAmount = (Spinner) findViewById(R.id.SP_yoloAmount);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        yoloAmount.setAdapter(adapter);

        //opencv 로드
        boolean load = OpenCVLoader.initDebug();
        if (load) {
            Log.i("MainActivity", "Open CV Libraries loaded...");
        } else {
            Log.i("MainActivity", "Open CV Libraries not loaded...");
        }

        //yolo 이미지 객체인식
        YOLO();

        db = dbReference.child("FoodInfo").child(uid).child(dbDate);


        //카메라 사진 촬영 소스
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        btn_photo = findViewById(R.id.btn_photo);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) { Log.d(TAG, "권한 설정 완료"); } else { Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(YoloActivity.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { switch (v.getId()) {
                case R.id.btn_photo:
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                    break; }
            }
        });


        //갤러리 사진 가져오기 소스
        Button btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //음식의 이름을 가져와 Textview에 출력
        addIngredients=(TextView)findViewById(R.id.addIngredients);

        //음식 정보 추가
        btn_addFood= (Button)findViewById(R.id.btn_addFood);
        btn_addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foodName != null) {
                    addFoodDB();
                    /*
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                     */

                }
                else {
                    Toast.makeText(YoloActivity.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // 식사 타입 선택(spinner)--------------------------------------------------------------------------
    public void selectmeal() {
        adapterMeal = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spmeal);
        adapterMeal.setDropDownViewResource(android.R.layout.simple_spinner_item);
        MealType.setAdapter(adapterMeal);
        MealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spmeal[position]) {
                    case "아침":
                        threeMeal = "Breakfast";
                        break;
                    case "점심":
                        threeMeal = "Lunch";
                        break;
                    case "저녁":
                        threeMeal = "Dinner";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    // 권한 요청
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]); } }


    // 카메라로 촬영한 사진의 썸네일을 가져와 이미지뷰에 띄워줌
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && intent.hasExtra("data")) {
                    bitmap = (Bitmap) intent.getExtras().get("data");
                    if (bitmap != null) {
                        //iv_photo.setImageBitmap(bitmap);
                        detect_food(bitmap);
                    }
                }
                break;
            case GET_GALLERY_IMAGE:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    try {
                        // 선택한 이미지에서 비트맵 생성
                        InputStream in = getContentResolver().openInputStream(intent.getData());
                        bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        detect_food(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    //객체검출을 통한 음식의 이름과 DB store에 저장된 음식의 이름이 일치하면 해당 음식의 영양성분 정보를 가져옴
    public void getFoodData() {
        DocumentReference docRef = dbStore.collection("FOOD").document(foodName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Map<String, Object> storeInfo = (Map<String, Object>) document.getData();

                    calory = Double.valueOf(String.valueOf(storeInfo.get("칼로리")));
                    carb = Double.valueOf(String.valueOf(storeInfo.get("탄수화물")));
                    protein = Double.valueOf(String.valueOf(storeInfo.get("단백질")));
                    fat = Double.valueOf(String.valueOf(storeInfo.get("지방")));


                    yoloAmount.setSelection(3);
                    sCalory = String.valueOf((int)(calory * calAmount));
                    sCarb = String.valueOf((int)(carb * calAmount));
                    sProtein = String.valueOf((int)(protein * calAmount));
                    sFat = String.valueOf((int)(fat * calAmount));

                    tvYoloCalory.setText(sCalory+ " Kcal");
                    tvYoloCarb.setText(sCarb + "g");
                    tvYoloProtein.setText(sProtein+ "g");
                    tvYoloFat.setText(sFat+ "g");

                    selectAmount();
                }
                else {

                }
            }
        });

    }


    // 음식 인분 수 스피너
    public void selectAmount() {

        yoloAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // "0.25", "0.5", "0.75", "1", "1.25", "2", "3"
                switch (spItems[position]) {
                    case "1/4 인분":
                        calAmount = 0.25;
                        break;
                    case "1/2 인분":
                        calAmount = 0.5;
                        break;
                    case "3/4 인분":
                        calAmount = 0.75;
                        break;
                    case "1 인분":
                        calAmount = 1;
                        break;
                    case "1.5 인분":
                        calAmount = 1.5;
                        break;
                    case "2 인분":
                        calAmount = 2;
                        break;
                    case "3 인분":
                        calAmount = 3;
                        break;
                }

                // 음식 인분수 계산
                sCalory = String.valueOf((int)(calory * calAmount));
                sCarb = String.valueOf((int)(carb * calAmount));
                sProtein = String.valueOf((int)(protein * calAmount));
                sFat = String.valueOf((int)(fat * calAmount));

                tvYoloCalory.setText(sCalory+ " Kcal");
                tvYoloCarb.setText(sCarb + "g");
                tvYoloProtein.setText(sProtein+ "g");
                tvYoloFat.setText(sFat+ "g");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //DB에 업로드 및 업데이트
    public void addFoodDB() {

        db.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                DailyInfo dInfo = dataSnapshot.getValue(DailyInfo.class);
                if(dInfo != null){
                    // Get DailyInfo
                    String cal = dInfo  .getDailyCalory();
                    String carb = dInfo.getDailyCarb();
                    String protein = dInfo.getDailyProtein();
                    String fat = dInfo.getDailyFat();

                    int dCalory = Integer.valueOf(cal) + Integer.valueOf(sCalory);
                    int dCarb = Integer.valueOf(carb) + Integer.valueOf(sCarb);
                    int dProtein = Integer.valueOf(protein) + Integer.valueOf(sProtein);
                    int dFat = Integer.valueOf(fat) + Integer.valueOf(sFat);

                    HashMap dailyResult = new HashMap();
                    dailyResult.put("DailyCalory", String.valueOf(dCalory));
                    dailyResult.put("DailyCarb", String.valueOf(dCarb));
                    dailyResult.put("DailyProtein", String.valueOf(dProtein));
                    dailyResult.put("DailyFat", String.valueOf(dFat));

                    db.updateChildren(dailyResult);

                }
                else {

                    HashMap dailyResult = new HashMap();

                    dailyResult.put("DailyCalory", String.valueOf(sCalory));
                    dailyResult.put("DailyCarb", String.valueOf(sCarb));
                    dailyResult.put("DailyProtein", String.valueOf(sProtein));
                    dailyResult.put("DailyFat", String.valueOf(sFat));

                    db.updateChildren(dailyResult);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        // FoodName - 칼, 3대영양소, 이름
        HashMap result = new HashMap<>();

        result.put("FoodName", foodName);
        result.put("Calory", sCalory);
        result.put("Carb", sCarb);
        result.put("Protein", sProtein);
        result.put("Fat", sFat);

        db.child(threeMeal).child(foodName).setValue(result);

        //DB에 촬영 및 갤러리에서 가져온 사진 정보를 저장
        ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baOutStream);
        byte[] data = baOutStream.toByteArray();
        
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업로드중...");
        progressDialog.show();

        //DB storage의 업로드하는 사진의 저장경로 및 이름 설정
        StorageReference stRef = storage.getReferenceFromUrl(storageRef + "FoodInfo/" + uid + "/" + dbDate + "/" + threeMeal +"/" + foodName + ".jpg");
        UploadTask uploadTask = stRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(YoloActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(YoloActivity.this, "사진 업로드 성공", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(YoloActivity.this, MainActivity.class));
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //음식의 사진과 영양성분이 DB에 업로드 되기전까지 로딩화면 출력
                double progress = (100 * snapshot.getBytesTransferred()) /  snapshot.getTotalByteCount();
                //dialog에 진행률을 퍼센트로 출력해 준다
                progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
            }
        });
    }
}

