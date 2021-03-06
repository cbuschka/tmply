package com.github.cbuschka.tmply.business;

import com.github.cbuschka.tmply.domain.bucket.BucketDomainService;
import com.github.cbuschka.tmply.domain.bucket.BucketEntity;
import com.github.cbuschka.tmply.domain.session.WebSocketSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class PublishBucketBusinessService
{
	@Autowired
	private BucketDomainService bucketDomainService;
	@Autowired
	private WebSocketSessionRepository webSocketSessionRepository;
	@Autowired
	private DeliverBucketBusinessService deliverBucketBusinessService;

	public BucketDto publish(BucketDto request)
	{
		WebSocketSession subscriber = this.webSocketSessionRepository.getSubscriber(request.getBucketName());
		if (subscriber != null)
		{
			this.deliverBucketBusinessService.deliver(request.getBucketName(), request.getData(), subscriber);
			this.webSocketSessionRepository.unsubscribe(request.getBucketName(), subscriber);
			return request;
		}
		else
		{
			BucketEntity bucketEntity = this.bucketDomainService.putBucket(request.getBucketName(), request.getData());
			return new BucketDto(bucketEntity.getBucketName(), bucketEntity.getData());
		}
	}
}
