package com.kuple.zone.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuple.zone.Adapter.ChildAdapter;
import com.kuple.zone.Adapter.HeaderAdapter;
import com.kuple.zone.Adapter.HorizentalAdapter;
import com.kuple.zone.R;
import com.kuple.zone.board.CommonboardActivity;
import com.kuple.zone.model.HeaderModel;
import com.kuple.zone.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment {
    private RecyclerView recyclerview;
    private RecyclerView horizentalRecyclerView;
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        recyclerview =view.findViewById(R.id.board_recyclerview_title);
        horizentalRecyclerView=view.findViewById(R.id.board_favorite);

        final List<String> data = new ArrayList<>();
        data.add("커뮤니티");
        data.add("쿠플웹진");
        data.add("학업정보");
        data.add("생활정보");
        data.add("교내단체게시판");
        HeaderAdapter headerAdapter=new HeaderAdapter(data,getContext(),getActivity());
        recyclerview.setAdapter(headerAdapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        horizentalRecyclerView.setLayoutManager(layoutManager);


        mStore.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final UserModel userModel=documentSnapshot.toObject(UserModel.class);
                assert userModel != null;

                HorizentalAdapter adapter=new HorizentalAdapter(getContext(),userModel.getFavoritList());
                adapter.setOnIemlClickListner(new ChildAdapter.OnItemClickListener() {
                    @Override
                    public void onitemClick(View v, int pos) {
                        Intent intent=new Intent(getContext(), CommonboardActivity.class);
                        intent.putExtra("BoardName",userModel.getFavoritList().get(pos));
                        startActivity(intent);
                    }
                });
                horizentalRecyclerView.setAdapter(adapter);
            }
        });





        return view;
    }

}