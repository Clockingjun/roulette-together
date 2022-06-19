package com.stir.roulette.web;

import com.stir.roulette.service.GameService;
import com.stir.roulette.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final GameService gameService;

    @GetMapping("/")
    public String index(ModelMap model, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        System.out.println("> X-FORWARDED-FOR : " + ip);

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println("> Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println(">  WL-Proxy-Client-IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            System.out.println("> HTTP_CLIENT_IP : " + ip);
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            System.out.println("> HTTP_X_FORWARDED_FOR : " + ip);
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
            System.out.println("> getRemoteAddr : "+ip);
        }
        System.out.println("> Result : IP Address : "+ip);

        model.addAttribute("game", gameService.findMyGame());
        model.addAttribute("data", "Hello Spring!");
        model.addAttribute("msg", 11);
        return "index";
    }

    @GetMapping("/FncUserData")
    public String FncUserData(ModelMap model, @RequestParam(value="key") String key) {

        System.out.println("하하하하하" + key);
        return "index";
    }

    @GetMapping("/gameSetting")
    public String gameSetting(ModelMap model) {
        model.addAttribute("data", "Hello Spring!");
        model.addAttribute("msg", 11);
        return "gameSetting";
    }

//    @GetMapping("/rt/game/v1/games/{id}")
//    public Long findByGameCode(@PathVariable Long gameCode) {
//        return gameService.findByGameCode(gameCode);
//    }

    @ResponseBody
    @PostMapping("/saveRouletteImg")
    public String saveRouletteImg(HttpServletRequest request, String strImg, String gameCode) throws Throwable{
        String uploadpath="uploadImage\\";
        //String folder=request.getServletContext().getRealPath("/") +uploadpath;
        String folder = "C:\\" + uploadpath;
        String fullpath="";
        String[] strParts=strImg.split(",");
        String rstStrImg=strParts[1];  //,로 구분하여 뒷 부분 이미지 데이터를 임시저장
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_hhmmss");
       // String filenm=sdf.format(new Date()).toString()+"_testimg2.png";
        String filenm = gameCode + ".png";
        BufferedImage image=null;
        byte[] byteImg;
        BASE64Decoder decoder = new BASE64Decoder();
        byteImg = decoder.decodeBuffer(rstStrImg);  //base64 디코더를 이용하여 byte 코드로 변환
        ByteArrayInputStream bis= new ByteArrayInputStream(byteImg);
        image= ImageIO.read(bis);   //BufferedImage형식으로 변환후 저장
        bis.close();

        fullpath=folder+filenm;
        File folderObj= new File(folder);
        if( !folderObj.isDirectory() )
            folderObj.mkdir();
        File outputFile= new File(fullpath);  //파일객체 생성
        if( outputFile.exists() )
            outputFile.delete();
        ImageIO.write(image, "png", outputFile); //서버에 파일로 저장
        return uploadpath+filenm;
    }

    @RequestMapping(value="/loadImage.do")
    public String displayPhoto(@RequestParam(value="fileId") String fileId, @RequestParam(value="gameCode") String gameCode, HttpServletResponse response)throws Exception{

        response.setContentType("image/jpg");
        ServletOutputStream bout = response.getOutputStream();
        //파일의 경로
        String imgpath = "C:\\uploadImage"+File.separator+gameCode+".png";
        FileInputStream f = new FileInputStream(imgpath);
        int length;
        byte[] buffer = new byte[10];
        while((length=f.read(buffer)) != -1){
            bout.write(buffer,0,length);
        }
        f.close();

        System.out.println("여기서 지우자");

        File file = new File("C:\\uploadImage/" + gameCode +".png");
        if( file.exists() ){
            if(file.delete()){
                System.out.println("파일삭제 성공");
            } else {
                System.out.println("파일삭제 실패");
            }
        }else{
            System.out.println("파일이 존재하지 않습니다.");
        }
        return null;
    }

    @RequestMapping(value="/deleteImg.do")
    @ResponseBody
    public String deleteImg(@RequestParam(value="gameCode") String gameCode) throws Exception{
        System.out.println(gameCode+"ㅇ랑ㄹ");

     /*   File file = new File("C:\\uploadImage/" + gameCode +".png");
        if( file.exists() ){
            if(file.delete()){
                System.out.println("파일삭제 성공");
            } else {
                System.out.println("파일삭제 실패");
            }
        }else{
            System.out.println("파일이 존재하지 않습니다.");
        }*/
        return null;
    }

}
