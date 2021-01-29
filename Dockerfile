FROM amazon/aws-cli
COPY q3_data_update.sh .
COPY .aws /root/.aws
ENTRYPOINT [ ]
CMD sh q3_data_update.sh
