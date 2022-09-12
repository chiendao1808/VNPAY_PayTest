package com.example.vnpaytest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.vnpaytest.entities.Refund;

@Repository
@Transactional
public interface RefundRepository extends JpaRepository<Refund,Long> {

    @Query(value = "select * from refunds rf where \n "
        + "(:requestId =-1 or rf.request_id = :requestId) \n "
        + "and (:transactionRef ='ALL' or rf.transaction_ref = :transactionRef) \n "
        + "and (:transactionNo ='ALL' or rf.transaction_no = :transactionNo)",nativeQuery = true)
   Page<Refund> getByCreterias(Long requestId, String transactionRef, String transactionNo,Pageable pageable);

}
