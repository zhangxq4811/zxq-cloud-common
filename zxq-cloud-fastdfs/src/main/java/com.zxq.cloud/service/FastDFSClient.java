package com.zxq.cloud.service;

import com.github.tobato.fastdfs.domain.FileInfo;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zxq
 */
@Component
public class FastDFSClient {
	

    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    
    /**
	 * fastDFS 从本地读取文件上传
	* @param pathname  文件名加路径
	* @return String  fastDFS中的文件名称:group1/M00/01/04/CgMKrVvS0geAQ0pzAACAAJxmBeM793.doc
	* @throws Exception
	 */
	public String uploadFile(String pathname) throws Exception{
		File file = new File(pathname);
		FileInputStream inputStream = new FileInputStream (file);
        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, file.length(), FilenameUtils.getExtension(file.getName()),null);
        //storePath.getFullPath()
        return storePath.getFullPath();
	}
	
	/**
	 * fastDFS 上传文件
	* @param file  上传的文件
	* @return String  fastDFS中的文件名称:group1/M00/01/04/CgMKrVvS0geAQ0pzAACAAJxmBeM793.doc
	* @throws Exception
	 */
	public String uploadFile(MultipartFile file) throws Exception{
		byte[] bytes = file.getBytes();
		long fileSize = file.getSize();
	    String fileName = file.getOriginalFilename();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		StorePath storePath = fastFileStorageClient.uploadFile(byteArrayInputStream, fileSize, FilenameUtils.getExtension(fileName), null);
        return storePath.getFullPath();
	}
	
	/**
	 * 查看文件信息
	* @param groupName 存放文件节点组
	* @param filePath 存放文件路径加文件名(除去节点组的)
	 */
	public FileInfo queryFileInfo(String groupName,String filePath){
		return fastFileStorageClient.queryFileInfo(groupName, filePath);
	}
	
	/**
	 * 用于小文件下载，当文件太大时容易造成内存溢出
	* @param fileName  生成文件名
	* @param groupName 存放文件节点组
	* @param remoteFilename 存放文件路径加文件名
	* @return 
	* @throws IOException
	 */
	public File downloadFile(String fileName ,String groupName,String remoteFilename) throws IOException  {
		byte[] bytes = fastFileStorageClient.downloadFile(groupName, remoteFilename, new DownloadByteArray());
		File f = new File(fileName);
		if(!f.exists()){
			f.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(f);
		out.write(bytes);
		out.close();
		return f;
	}
	
	/**
	 * 删除文件
	 * @param filePath	47.102.98.244/group1/M00/00/00/rBMexF1eSPqADNTmAABN-gNR3KQ749.jpg
	 * @throws IOException
	 */
	public void deleteFile(String filePath) throws IOException {
		String groupName = filePath.substring(0, filePath.indexOf("/"));
		String remoteFilename = filePath.substring(filePath.indexOf("/") + 1);
		deleteFile(groupName, remoteFilename);
	}

	/**
	 * 删除文件
	 * @param groupName
	 * @param remoteFilename
	 */
	public void deleteFile(String groupName,String remoteFilename){
		fastFileStorageClient.deleteFile(groupName, remoteFilename);
	}

}
