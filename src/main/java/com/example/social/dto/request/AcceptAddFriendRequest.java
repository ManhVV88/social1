package com.example.social.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcceptAddFriendRequest {

	//biến boolean không dc đặt is ở trc vì sẽ bị lỗi ánh xạ khi lombook tao getter setter thì sẽ ko có method getIsAccept
	//mà chỉ có isAccept vì vậy dẫn đến việc ánh xạ bị sai , nên khi gửi postman sẽ có giá trị auto bằng false 
	// còn swagger sẽ sinh ra thêm 1 trường giá trị và bỏ is đi để nhận biết giá trị đúng 
	//https://stackoverflow.com/questions/21913955/json-post-request-for-boolean-field-sends-false-by-default	
	boolean accept;
}
