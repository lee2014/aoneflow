package com.aoneai.workflow.manager.dao

import java.util
import java.util.Optional

import com.aoneai.workflow.manager.entity.Job
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


/**
  * Created By chengli at 03/04/2018
  */
@Repository
trait JobRepository extends JpaRepository[Job, String] {

  /**
    * 根据job名称判断是否已经存在对应job信息
    *
    * @param jobName
    * @return
    */
  @Query(value = "select count(job_name) from jobs where job_name = :jobName", nativeQuery = true)
  def existsByJobName(@Param("jobName") jobName: String): Int

  /**
    * 查询job状态
    *
    * @param jobName
    * @return
    */
  @Query(value = "select status from jobs where job_name = :jobName", nativeQuery = true)
  def getStatusByJobName(@Param("jobName") jobName: String): Optional[String]


  @Query(value = "select * from jobs where status = :status", nativeQuery = true)
  def getByStatus(@Param("status") status: String): util.List[Job]

  /**
    * 获取所有job
    *
    * @return
    */
  @Query(value = "select * from jobs where 1=1 order by ?#{#pageable} ", countQuery = "select count(job_name) from jobs",nativeQuery = true)
  def getAllJobByPage(pageable: Pageable): Page[Job]

  @Query(value = "select * from jobs where status = :status order by ?#{#pageable}", countQuery = "select count(job_name) from jobs where status = :status", nativeQuery = true)
  def getByStatusAndPage(@Param("status") status: String, pageable: Pageable): Page[Job]

  @Query(value = "select * from jobs where job_name like :jobName order by ?#{#pageable}", countQuery = "select count(job_name) from jobs where job_name like :jobName", nativeQuery = true)
  def getByJobNameAndPage(@Param("jobName") jobName: String, pageable: Pageable): Page[Job]

  @Query(value = "select * from jobs where job_name like :jobName and status = :status order by ?#{#pageable}", countQuery = "select count(job_name) from jobs where job_name like :jobName and status = :status", nativeQuery = true)
  def getByJobNameAndStatusAndPage(@Param("jobName") jobName: String, @Param("status") status: String, pageable: Pageable): Page[Job]

  @Modifying
  @Query(value = "UPDATE jobs SET failed_time=failed_time+1 WHERE job_name=:jobName", nativeQuery = true)
  def updateAndIncJobFailedTime(@Param("jobName") jobName: String): Int

  @Modifying
  @Query("UPDATE Job job SET job.status='KILLED' WHERE job.status='RUNNING' or job.status = 'WAITING'")
  @Transactional
  def updateNotDoneJobWithKilled(): Int
}
