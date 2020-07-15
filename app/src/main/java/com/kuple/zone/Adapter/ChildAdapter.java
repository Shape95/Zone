package com.kuple.zone.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuple.zone.R;
import com.kuple.zone.model.BoardInfo;
import com.kuple.zone.model.HeaderModel;
import com.kuple.zone.model.UserModel;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;


public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private List<String> mChildList;
    private Context mContext;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    public ChildAdapter(List<String>mTitleList,Context mContext){
        this.mChildList=mTitleList;
        this.mContext=mContext;
    }
    public interface OnItemClickListener{
        void onitemClick(View v,int pos);
    }
    private static OnItemClickListener mListener=null;
    public void setOnIemlClickListner(ChildAdapter.OnItemClickListener listner){
        mListener=listner;
    }

    @NonNull
    @Override
    public ChildAdapter.ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildAdapter.ChildViewHolder holder, final int position) {
        final String data = mChildList.get(position);
       final DocumentReference df =mStore.collection("users").document(firebaseUser.getUid());
        holder.textView.setText(data);
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,"구독하기",Toast.LENGTH_SHORT).show();
//            }
//        });
        holder.favoritbtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                df.update("favoritList", FieldValue.arrayUnion(mChildList.get(position)));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                df.update("favoritList", FieldValue.arrayRemove(mChildList.get(position)));
            }
        });
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final UserModel userModel = documentSnapshot.toObject(UserModel.class);
                if (userModel.getFavoritList().contains(mChildList.get(position))) {//이미 좋아요 누른사람은
                    holder.favoritbtn.setLiked(true);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mChildList.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        LikeButton favoritbtn;
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.child_title);
            //imageView=itemView.findViewById(R.id.child_image);
            favoritbtn=itemView.findViewById(R.id.item_child_likeButton);

            itemView.setOnClickListener(new View.OnClickListener() {//클릭했을때
                @Override
                public void onClick(View v) {//들어가는 기능 detail로
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener!=null){
                            mListener.onitemClick(v,pos);
                        }
                    }
                }
            });

        }

    }
}
