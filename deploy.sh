echo "##########(ECR 에 로그인)##########"
aws ecr get-login-password --region ap-northeast-2 --profile weekand-server |
docker login --username AWS --password-stdin 256165122645.dkr.ecr.ap-northeast-2.amazonaws.com || exit 1
echo "##########(Gradle build)##########"
./gradlew -x test clean build || exit 2
echo "##########(이미지 build)##########"
docker build --platform amd64 -t weekand-server . || exit 3
echo "##########(이미지 tagging)##########"
docker tag weekand-server:latest 256165122645.dkr.ecr.ap-northeast-2.amazonaws.com/weekand-server:latest || exit 4
echo "##########(이미지 AWS ECR 에 push)##########"
docker push 256165122645.dkr.ecr.ap-northeast-2.amazonaws.com/weekand-server:latest || exit 5
